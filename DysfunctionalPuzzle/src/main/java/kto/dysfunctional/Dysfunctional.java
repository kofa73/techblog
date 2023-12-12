package kto.dysfunctional;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class Dysfunctional {

    public static void main(String[] args) {
        var function = f();
        // replace these two assignments
        var item1 = "change";
        var item2 = "these";

        boolean failed = (item1 != item2)
                // null-safe equals, so no NPE in case item1 or item2 is null
                || Objects.equals(item1, item2);

        for (int i = 0; i < 1000 && !failed; i++) {
            var result1 = function.apply(item1);
            var result2 = function.apply(item2);
            // Add some randomness to the checks, so people don't do weird things in an overridden equals,
            // exploiting the predictable order to return true/false; also, no order-based tricks in
            // the Function returned by f().
            failed =
                switch (ThreadLocalRandom.current().nextInt(5)) {
                    case 0 -> Objects.equals(result1, result2); // different argument -> different result
                    case 1 -> Objects.equals(item1, result1);   // not an identity function
                    case 2 -> Objects.equals(item2, result2);
                    case 3 -> !Objects.equals(result1, function.apply(item1)); // result is stable
                    case 4 -> !Objects.equals(result2, function.apply(item2));
                    default -> throw new IllegalArgumentException("Uncovered case");
                };
        }

        if (failed) {
            System.out.println("Try again.");
        } else {
            System.out.println("Success!");
        }
    }

    // change <String, String>, if required
    private static Function<String, String> f() {
        // replace the method reference / provide a lambda
        return String::toUpperCase;
    }
}
