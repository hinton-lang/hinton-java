# The Hinton Language

![Hinton Logo](Assets/Logos/Logo-wide.png)

This is a simple interpreter written in Java for a programming language called Hinton. The project is an extension of the code found in the book [Crafting Interpreters](https://craftinginterpreters.com/) by Bob Nystrom.

## Hinton's Syntax
This is an example of what Hinton's future syntax would look like. Hinton should be a programming language that can be accessible by anyone, with any level of programming experience. In Hinton, explicitness is the key.

```swift
/**
 * This is a multiline comment
 * The following is a recursive function
 */
func factorial(n: Int = 0): Int { // default parameters
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

// This is a constant
const welcome = "Welcome to the program\n";

// Printing to the console
print(welcome)

// This is a variable.
// The `input(message: String)` is a function to
// obtain user input from the console.
// The `int(x)` is a function to convert the given
// argument to an integer.
var x = int(input("Compute the factorial of: "));
print(factorial(n = x)) // named arguments

// While loops
while(true) {
    var again = input("Compute another? (y/n): ");
    
    if (again equals "y") {
        var x = int(input("Compute the factorial of: "));
        print(factorial(x))
        continue;
    }

    if (again equals "n") break;
}

// This is a dictionary
var myDict = {
    a: 3,
    b: "hello dictionary!!",
    c: {
        arr: [1, 2, 3],
        pow: fn(a, b) -> { // Lambda expression
            return a ** b;
        }
    },
    d: null
}

print(myDict)
print(type(myDict))
print(myDict.c.pow(2, 3))

// The range operator
var theRange = 100..1;

// indexed for-loops
for (var x = 0; x < theRange.length(); x++) {
    print(x)
}

// for-in loops
for (var item in theRange) {
    print(item)
}
```

## The current state of Hinton
The project is still in the "pre-development" stages. This means that anything about the language can change as more experience is acquired.

### Benchmarking
Although speed in not a top priority at these stages of the project, it is important to note that Hinton is very slow. Many improvements can be made to the scanner, parser, and interpreter to make Hinton a more reliable language, but those improvements will only happen once the major parts of the project have been completed. A benchmarking program can be found in the [testing](https://github.com/hinton-lang/Hinton/tree/master/test) folder.

As of right now, looping through 1,000,000 array elements takes `~430ms`. In comparison, the same program in Python takes `~40ms`. 😢

## To-do List
- [ ] Parse & Execute Expressions.
    - [x] Arithmetic expressions.
    - [x] Logical expressions.
    - [x] Array literals.
    - [x] Function calls.
    - [x] Lambda expressions.
    - [x] Member access expressions.
    - [x] Named arguments.
    - [x] Range expressions.
    - [x] String literals.
    - [ ] Binary (0b), Hexadecimal (0x), and Octal (0o) numbers
- [ ] Parse & Execute Statements.
    - [x] Constant declarations.
    - [x] Control flow statements.
    - [x] Dictionary declarations.
    - [x] Enum declarations.
    - [x] Function declarations.
    - [x] Looping statements.
    - [x] Named parameters.
    - [x] Variable declarations.
    - [ ] Class declarations.
    - [ ] For-in loops.
- [x] Define Built-In Types as Objects
    - [x] Array objects.
    - [x] Boolean objects.
    - [x] Dictionary objects.
    - [x] Enum objects.
    - [x] Function objects.
    - [x] Integer objects.
    - [x] Real objects.
    - [x] String objects.
- [ ] Create a Runtime Standard Library.
- [ ] Add Static Typing.
- [ ] Create 'Cosmo'. Hinton's official CLI.
