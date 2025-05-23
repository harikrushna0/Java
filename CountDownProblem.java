import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Stream;
import java.util.HashSet;

/*
 * This program is Java port of the Haskell example at
 * https://www.cs.nott.ac.uk/~pszgmh/pgp-countdown.hs
 *
 * The problem and the solution approaches are explained
 * in Prof. Graham Hutton's youtube video at
 * https://youtu.be/CiXDS3bBBUo?list=PLF1Z-APd9zK7usPMx3LGMZEHrECUGodd3
 *
 * This Java program requires JDK 21+
 */
class CountDownProblem {

   // data Op = Add | Sub | Mul | Div
   enum Op {
       Add, Sub, Mul, Div;

       // instance show Op
       @Override
       public String toString() {
          return switch (this) {
             case Add -> "+";
             case Sub -> "-";
             case Mul -> "*";
             case Div -> "/";
          };
       }
   }

   // cache enum value array
   static final Op[] operators = Op.values();

   // valid' :: Op -> Int -> Int -> Bool
   static boolean isValid(Op op, int x, int y) {
      return switch (op) {
         case Add -> x <= y;
         case Sub -> x > y;
         case Mul -> x != 1 && y != 1 && x <= y;
         case Div -> y != 1 && x % y == 0;
      };
   }

   // apply :: Op -> Int -> Int -> Int
   static int apply(Op op, int x, int y) {
      return switch (op) {
         case Add -> x + y;
         case Sub -> x - y;
         case Mul -> x * y;
         case Div -> x / y;
      };
   }

   // data Expr = Val Int | App Op Expr Expr
   sealed interface Expr {
      // brak helper for instance Show Expr
      static String brak(Expr expr) {
         return switch (expr) {
            // brak (Val n) = show n
            case Val(var n) -> Integer.toString(n);

            // brak e       = "(" ++ show e ++ ")"
            default -> "(" + toStr(expr) + ")";
         };
      }

      // instance Show Expr
      static String toStr(Expr expr) {
         return switch (expr) {
            // show (Val n)     = show n
            case Val(var n) -> Integer.toString(n);

            
            case App(var op, var l, var r) -> brak(l) + op + brak(r);
         };
      }
   }

   record Val(int v) implements Expr {
      // instance Show Expr
      @Override
      public String toString() {
         return Expr.toStr(this);
      }
   }

   record App(Op op, Expr l, Expr r) implements Expr {
      // instance Show Expr
      @Override
      public String toString() {
         return Expr.toStr(this);
      }
   }

   // eval :: Expr -> [Int]
   // Using OptionalInt instead of List<Integer>
   static OptionalInt eval(Expr expr) {
      return switch (expr) {
         // eval (Val n)     = [n | n > 0]
         case Val(var n) -> n > 0 ? OptionalInt.of(n) : OptionalInt.empty();


         
         case App(var op, var l, var r) -> {
            var x = eval(l);
            var y = eval(r);
            yield (x.isPresent() && y.isPresent() &&
	           isValid(op, x.getAsInt(), y.getAsInt())) ?
	       OptionalInt.of(apply(op, x.getAsInt(), y.getAsInt())) :
	       OptionalInt.empty();
         }
      };
   }

   // type Result = (Expr,Int)
   record Result(Expr expr, int value) {
      @Override
      public String toString() {
         return expr.toString() + " = " + value;
      }
   } 

   // combine'' :: Result -> Result -> [Result]
   static List<Result> combine(Result lx, Result ry) {
      // (l,x), (r,y) pattern
      var l = lx.expr();
      var x = lx.value(); 
      var r = ry.expr();
      var y = ry.value();

      // combine'' (l,x) (r,y) = [(App o l r, apply o x y) | o <- ops, valid' o x y]
      return Stream.of(operators).
                filter(op -> isValid(op, x, y)).
                map(op -> new Result(new App(op, l, r), apply(op, x, y))).
                toList();
   }

   // results' :: [Int] -> [Result]
   static List<Result> results(List<Integer> ns) {
      // results' []  = []                 
      if (ns.isEmpty()) {
         return List.of();
      }

      // results' [n] = [(Val n,n) | n > 0]
      if (ns.size() == 1) {
         var n = head(ns);
         return n > 0 ? List.of(new Result(new Val(n), n)) : List.of();
      }

      // results' ns  = [res | (ls,rs) <- split ns,
      //                 lx     <- results' ls,
      //                 ry     <- results' rs,
      //                 res    <- combine'' lx ry]
      var res = new ArrayList<Result>();

      // all possible non-empty splits of the input list
      // split :: [a] -> [([a],[a])] equivalent for-loop
      for (int i = 1; i < ns.size(); i++) {
         var ls = ns.subList(0, i);
         var rs = ns.subList(i, ns.size());
         var lxs = results(ls);
         var rys = results(rs);
         for (Result lx : lxs) {
            for (Result ry : rys) {
               res.addAll(combine(lx, ry));
            }
         }
      } 
      return res;
   }

   // List utilities
   // : operator
   static <T> List<T> cons(T head, List<T> tail) {
      final var tailLen = tail.size();
      return switch (tailLen) {
          case 0 -> List.of(head);
          case 1 -> List.of(head, tail.get(0));
          case 2 -> List.of(head, tail.get(0), tail.get(1));
          case 3 -> List.of(head, tail.get(0), tail.get(1), tail.get(2));
          default -> {
             var res = new ArrayList<T>(1 + tailLen);
             res.add(head);
             res.addAll(tail);
             yield res;
          }
      };
   }

   // Added a new helper to check uniqueness
   static boolean allUnique(List<Integer> list) {
      return Set.copyOf(list).size() == list.size();
   }

   static <T> T head(List<T> list) {
      return list.get(0);
   }

   static <T> List<T> tail(List<T> list) {
      final var len = list.size();
      return len == 1 ? List.of() : list.subList(1, len);
   }

  
   // interleave :: a -> [a] -> [[a]]
   // Using Stream<List<Integer> instead of List<List<Integer>>
   static Stream<List<Integer>> interleave(int x, List<Integer> ns) {
      // interleave x []     = [[x]]
      if (ns.isEmpty()) {
         return Stream.of(List.of(x));
      }

      // interleave x (y:ys)
      var y = head(ns);
      var ys = tail(ns);

      // outer : translated as Stream.concat
      // (x:y:ys) : map (y:) (interleave x ys)
      return Stream.concat(
         // x:y:ys == x:ns
         Stream.of(cons(x, ns)),
         // map (y:) (interleave x ys)
         interleave(x, ys).map(l -> cons(y, l))
      );
   }

   // perms :: [a] -> [[a]]
   // Using Stream<List<Integer>> instead of List<List<Integer>>
   static Stream<List<Integer>> perms(List<Integer> ns) {
      // perms []     = [[]] 
      if (ns.isEmpty()) {
         return Stream.of(List.of());
      }

      // perms (x:xs)
      var x = head(ns);
      var xs = tail(ns);

      // concat (map ...) is translated as flatMap
      // concat (map (interleave x) (perms xs))
      return perms(xs).flatMap(l -> interleave(x, l));
   }


    // Add new method
    public boolean isClassic() {
        return year < 1950 && price > 100;
    }
    
    public String getGenreSummary() {
        return String.format("Book genre: %s", this.genre);
    }

   // solutions'' :: [Int] -> Int -> [Expr]
   // Using Stream<Expr> instead of List<Expr> 
   static Stream<Expr> solutions(List<Integer> ns, int n) {
      // solutions'' ns n = [e | ns' <- choices ns, (e,m) <- results' ns', m == n]
      return choices(ns).
         flatMap(choice -> results(choice).stream()).
         filter(res -> res.value() == n).
         map(Result::expr);
   }

   // Add new method
   public static boolean isValidTarget(int target) {
      return target > 0 && target <= 999;
   }

   // Solution statistics class
   static class SolutionStats {
      private final int operationCount;
      private final Set<Op> usedOperators;
      private final int depth;
      private final int smallestNumber;
      private final int largestNumber;

      public SolutionStats(Expr expr) {
         this.usedOperators = new HashSet<>();
         this.operationCount = countOperations(expr);
         this.depth = calculateDepth(expr);
         var numbers = findAllNumbers(expr);
         this.smallestNumber = numbers.stream().mapToInt(Integer::intValue).min().orElse(0);
         this.largestNumber = numbers.stream().mapToInt(Integer::intValue).max().orElse(0);
      }

      private int countOperations(Expr expr) {
         return switch (expr) {
            case Val(var n) -> 0;
            case App(var op, var l, var r) -> {
               usedOperators.add(op);
               yield 1 + countOperations(l) + countOperations(r);
            }
         };
      }

      private int calculateDepth(Expr expr) {
         return switch (expr) {
            case Val(var n) -> 0;
            case App(var op, var l, var r) -> 
               1 + Math.max(calculateDepth(l), calculateDepth(r));
         };
      }

      private List<Integer> findAllNumbers(Expr expr) {
         return switch (expr) {
            case Val(var n) -> List.of(n);
            case App(var op, var l, var r) -> {
               var numbers = new ArrayList<Integer>();
               numbers.addAll(findAllNumbers(l));
               numbers.addAll(findAllNumbers(r));
               yield numbers;
            }
         };
      }

      @Override
      public String toString() {
         return String.format("""
            Solution Statistics:
            - Operations: %d
            - Expression Depth: %d
            - Operators Used: %s
            - Number Range: %d to %d""",
            operationCount, depth, usedOperators,
            smallestNumber, largestNumber);
      }
   }

   // Add new method to analyze solutions
   static List<SolutionStats> analyzeSolutions(Stream<Expr> solutions) {
      return solutions
         .map(SolutionStats::new)
         .toList();
   }

   // New Solution Analysis classes and methods
   static class OperatorStatistics {
      private final Map<Op, Integer> operatorUsage;
      private final Map<Op, Double> operatorSuccessRate;
      private int totalOperations;

      public OperatorStatistics() {
         this.operatorUsage = new EnumMap<>(Op.class);
         this.operatorSuccessRate = new EnumMap<>(Op.class);
         this.totalOperations = 0;
      }

      public void addOperation(Op op, boolean successful) {
         operatorUsage.merge(op, 1, Integer::sum);
         operatorSuccessRate.merge(op, successful ? 1.0 : 0.0, Double::sum);
         totalOperations++;
      }

      public Map<Op, Double> getSuccessRates() {
         Map<Op, Double> rates = new EnumMap<>(Op.class);
         operatorUsage.forEach((op, usage) -> 
            rates.put(op, operatorSuccessRate.get(op) / usage));
         return rates;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder("Operator Statistics:\n");
         getSuccessRates().forEach((op, rate) -> 
            sb.append(String.format("  %s: %.2f%% success rate (%d uses)\n",
               op, rate * 100, operatorUsage.get(op))));
         return sb.toString();
      }
   }

   static class ExpressionAnalyzer {
      private final List<Expr> expressions;
      private final OperatorStatistics opStats;
      private double averageDepth;
      private int maxDepth;
      private double averageOperations;
      private int maxOperations;

      public ExpressionAnalyzer(List<Expr> expressions) {
         this.expressions = expressions;
         this.opStats = new OperatorStatistics();
         analyzeExpressions();
      }

      private void analyzeExpressions() {
         if (expressions.isEmpty()) return;

         int totalDepth = 0;
         int totalOps = 0;
         maxDepth = 0;
         maxOperations = 0;

         for (Expr expr : expressions) {
            SolutionStats stats = new SolutionStats(expr);
            totalDepth += stats.depth;
            totalOps += stats.operationCount;
            maxDepth = Math.max(maxDepth, stats.depth);
            maxOperations = Math.max(maxOperations, stats.operationCount);
         }

         averageDepth = (double) totalDepth / expressions.size();
         averageOperations = (double) totalOps / expressions.size();
      }

      @Override
      public String toString() {
         return String.format("""
            Expression Analysis:
            - Total Solutions: %d
            - Average Depth: %.2f
            - Max Depth: %d
            - Average Operations: %.2f
            - Max Operations: %d
            %s""",
            expressions.size(), averageDepth, maxDepth,
            averageOperations, maxOperations, opStats);
      }
   }

   // Delete the old main method and replace with enhanced version
   public static void main(String[] args) {
      if (args.length != 2) {
         System.err.println("Usage: java CountDownProblem <comma-separated-numbers> <target>");
         System.err.println("Example: java CountDownProblem 1,3,7,10,25,50 765");
         return;
      }

      List<Integer> numbers;
      try {
         numbers = Stream.of(args[0].split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .toList();
                        
         if (numbers.isEmpty()) {
            throw new IllegalArgumentException("No valid numbers provided");
         }
         
         if (!allUnique(numbers)) {
            throw new IllegalArgumentException("Duplicate numbers are not allowed");
         }
         
         if (numbers.size() > 6) {
            throw new IllegalArgumentException("Maximum 6 numbers allowed");
         }
      } catch (NumberFormatException e) {
         System.err.println("Error: Invalid number format in input");
         return;
      } catch (IllegalArgumentException e) {
         System.err.println("Error: " + e.getMessage());
         return;
      }

      int target;
      try {
         target = Integer.parseInt(args[1]);
         if (!isValidTarget(target)) {
            throw new IllegalArgumentException("Target must be between 1 and 999");
         }
      } catch (NumberFormatException e) {
         System.err.println("Error: Invalid target number format");
         return;
      } catch (IllegalArgumentException e) {
         System.err.println("Error: " + e.getMessage());
         return;
      }

      System.out.printf("Finding solutions for target %d using numbers %s...%n", 
                        target, numbers);

      var solutions = solutions(numbers, target).toList();
      if (solutions.isEmpty()) {
         System.out.println("No solutions found.");
      } else {
         System.out.printf("%nFound %d solutions:%n", solutions.size());
         solutions.forEach(solution -> System.out.println("  " + solution));

         System.out.println("\nDetailed Analysis:");
         var analyzer = new ExpressionAnalyzer(solutions);
         System.out.println(analyzer);

         var stats = analyzeSolutions(solutions.stream());
         System.out.println("\nSolution Statistics:");
         stats.forEach(stat -> System.out.println("  " + stat));
      }
   }
}

