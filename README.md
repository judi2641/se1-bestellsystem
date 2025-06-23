# D1: Refactoring: *DataFactory*

[*"Refactoring"*](https://refactoring.guru/refactoring) is the process of
improving the code structure or capabilities without adding or changing
functionality.

In the [*"se1-bestellsystem"*](https://github.com/sgra64/se1-bestellsystem)
project, a simple class
[*Customer*](https://github.com/sgra64/se1-bestellsystem/blob/c12-customer/src/datamodel/Customer.java)
was created.

It has several shortcomings:

- Creation of objects of class *Customer* is possible everywhere, any number
    of *Customer* objects can be created without registration.

- Objects of class *Customer* can be created from almost arbitrary arguments,
    e.g. the uniqueness of *id* attributes is not gueranteed. Duplicates
    can be created.

- Class *Customer* includes *"program logic"* such as splitting single-String
    names, e.g. *"Meyer, Eric"* into first *"Eric"* and last *"Meyer"* name
    attributes.

While the first and second points are more of the nature of consistency
(duplicate id, duplicate objects), the third point refers to a structural
problem.

Goals of the *Refactoring* are:

1. Classes of the *datamodel* (*Customer, Article, Order*) should contain
    only data model aspects and not program logic.

1. More stringent validation should be introduced such that:

    - *Customer* objects are only created with valid names and contacts
        validated as email addresses or phone numbers.

1. Duplicates (multiple *Customer* objects with same *id*, but different
    name or contact information) should be avoided.

1. Improving consistentcy by turning *datamodel* classes into *"immutable"*
    classes, which are classes that have no setter methods, e.g. no
    `setId()` methods.

1. Introduction of the *"fluent"* naming scheme introduced by
    [*lombok Accessors*](https://projectlombok.org/features/experimental/Accessors)
    with removing the `get-` and `set-` prefixes of method names, e.g.
    `id()` rather than `getId()`.


Two new classes are introduced to centralize object creation and argument
validation:

- class *"DataFactory"* (according to the
    [*Factory*](https://refactoring.guru/design-patterns/factory-method)
    [*Software Design pattern*](https://refactoring.guru/design-patterns))
    centralizes the creation of *datamodel* objects of classes:
    *Customer, Article, Order*.

- class *"Validator"* centralizes the logic to validate input (*names*,
    *contacts*). It is used by class *DataFactory* to create objects only
    from validated input.

- Arbitrary creation of *datamodel* objects anywhere in the code with `new`
    is prevented by reducing the visibility of constructors to `protected`
    (constructors can be invoked only by classes of the *"datamodel"* package).

The UML Class Diagram shows the new classes created in effect of the refactoring
in the *datamodel* package:

- class `DataFactory` with factory methods, e.g. *createCustomer(...)*, to
    create objects such as of class *Customer* only from validated arguments.
    Class *DataFactory* also has an inner class `IdPool<ID>` to assign *id*
    from a central pool of unique *id*.

- class `Validator` is used to validate names and contact information (email
    or phone numbers) and

- class `Customer` has setter methods removed (*"immutable class"*) and
    uses the *"fluent"* naming scheme.

<img src="https://raw.githubusercontent.com/sgra64/se1-bestellsystem/refs/heads/markup/d12-datamodel/d1-DataFactory.png" alt="drawing" width="800"/>



Steps:

1. [Create a Singleton Class: *DataFactory*](#1-create-a-singleton-class-datafactory)

1. [Understand Inner Class *IdPool*](#2-understand-inner-class-idpool)

1. [Immutalize Class *Customer*](#3-immutalize-class-customer)

1. [Run Driver Code](#4-run-driver-code)

1. [Commit and Push Changes](#6-commit-and-push-changes)

<!-- 1. [Update JUnit-Tests](#5-update-junit-tests) -->



&nbsp;

## 1. Create a Singleton Class: *DataFactory*

Fetch the code drop
[*d1-datafactory*](https://github.com/sgra64/se1-bestellsystem/tree/d1-datafactory)
(fetch the branch and merge, or pull the branch).

Incoming files:

```
M src/application/Application.java
A src/application/Application_D1.java
M src/application/package-info.java
M src/datamodel/Customer.java
A src/datamodel/Validator.java
```

Create a new class `DataFactory` in package `datamodel` that implements the
[*Singleton Pattern*](https://refactoring.guru/design-patterns/singleton)
(chose whether *lazy* or *strict*).

Supplement class `DataFactory` with code and *javadoc* from the
[*gist*](https://gist.github.com/sgra64/4a926af94b1bc231a84f8baf84cf8a3e).


Understand method `Optional<Customer> createCustomer(String... args)`:

```java
/**
 * <i>Factory</i> method to create {@link Customer} object from arguments
 * as mix of name parts and contacts. No object is created from invalid
 * name parts or contacts.
 * <p>
 * Examples:
 * <pre>
 * - createCustomer("Eric", "Meyer", "eric98@yahoo.com", "eric98@yahoo.com", "(030) 3945-642298")
 * - createCustomer("Bayer, Anne", "anne24@yahoo.de", "(030) 3481-23352", "fax: (030)23451356")
 * - createCustomer(" Tim ", " Schulz-Mueller ", "tim2346@gmx.de")
 * - createCustomer("Nadine-Ulla Blumenfeld", "+49 152-92454")
 * - createCustomer("Khaled Saad Mohamed Abdelalim", "+49 1524-12948210")
 * </pre>
 * @param args name parts and contacts to create a {@link Customer} object
 * @return {@link Customer} object with valid name and contacts or empty
 */
public Optional<Customer> createCustomer(String... args) {
    /*
     * separate contacts from names in args
     */
    final StringBuilder flatName = new StringBuilder();
    final List<String> contacts = Arrays.stream(args)
        .map(arg -> {
            var contact = validator.validateContact(arg);
            if(contact.isEmpty()) {
                flatName.append(arg).append(" ");
            }
            return contact;
        })
        .flatMap(Optional::stream)
        .toList();
    // 
    return validator.validateName(flatName.toString())
        .map(np -> new Customer(customerIdPool.next(), np.lastName(), np.firstName(), contacts));
}
```

Answer questions:

1. What is the input of the method (see examples in *javadoc*)?

1. What is the result of the method?

1. Where is the actual *Customer* object created?

1. What does `.flatMap(Optional::stream)` do?

1. Why can't *Customer* objects be created outside package *datamodel*?



&nbsp;

## 2. Understand Inner Class *IdPool*

Read the following code and understand class `IdPool<ID>` and the use with
variable `customerIdPool` below:

```java
class IdPool<ID> {
    private final List<ID> ids;
    private final BiConsumer<List<ID>, Integer> idExpander;
    private int current = 0;

    /**
     * Constructor.
     * @param initial initial set of <i>id</i> stored in pool
     * @param expander call-out to expand <i>id</i> pool by given size
     */
    IdPool(List<ID> initial, BiConsumer<List<ID>, Integer> expander) {
        this.ids = new ArrayList<>(initial==null? List.of() : initial);
        this.idExpander = expander;
    }

    /**
     * Retrieve next <i>id</i> from pool. Expand, if exhausted.
     * @return next <i>id</i> from pool
     */
    ID next() {
        if(current >= ids.size()) {
            idExpander.accept(ids, 25);
        }
        return ids.get(current++);
    }
}

private final IdPool<Long> customerIdPool = new IdPool<>(
    List.of( // initial pool <i>id</i>
        892474L, 643270L, 286516L, 412396L, 456454L, 651286L
    ),
    (idPool, expandBy) -> // pool expander
        Stream.generate(() -> (long)((Math.random() * (999999 - 100000)) + 100000))
            .filter(id -> ! idPool.contains(id))
            .limit(expandBy)
            .forEach(idPool::add));
```

Answer questions:

1. What is *<ID>* ?

1. What does method *next()* return?

1. Where are *ids* stored?

1. What happens when the initial set of *id* is used-up?
    Point at the code (line) where this is detected.
    How many new *ids* are supplied into the pool?

1. What is the second argument of the *IdPool* constructor:
    `BiConsumer<List<ID>, Integer> expander`?

    - Where is this construct invoked?

    - What does it accept as arguments?

    - What does it produce as result?



&nbsp;

## 3. Immutalize Class *Customer*

<!-- An *immutable* class *Customer* does not allow changes to attributes.
*DataFactory* is the only class that can create new *Customer*
objects from validated parameters.

Making class *Customer* immutable means:
1. Make all attributes: `private` and `final`.
1. Remove *setter()* methods.
1. Remove all constructors, except one used by *DataFactory* with
    visibility: `protected` to prevent creation of *Customer*
    objects from other packages.
The remaining class has no *setter()* methods and hence is called *immutable*,
which is shown in the UML class diagram by stereotype: `<<immutable>>`.
<img src="Customer.png" alt="drawing" width="400"/>
-->

Study the immutalized class *Customer.class* that came with the code drop
and compare with the prior (none-immutalized) class.

Name three differences?



&nbsp;

## 4. Run Driver Code

The new driver code came with the code drop *Application_D1.java*.

Customer objects can no longer be directly created by `new Customer(...);`.
Instead, the *DataFactory* is used:


```java
/**
 * Class that implements the {@link Runner} interface with {@code run(String[] args)}
 * method that executes tasks of the <i>D1</i> assignment.
 *
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public class Application_D1 implements Runner {

    /**
     * Run code of the <i>C12</i> assignment.
     */
    @Override
    public void run(String[] args) {

        DataFactory df = DataFactory.getInstance();

        List<Customer> customers = List.of(
            df.createCustomer("Eric", "Meyer", "eric98@yahoo.com", "eric98@yahoo.com",
                "(030) 3945-642298"),
            df.createCustomer("Bayer, Anne", "anne24@yahoo.de", "(030) 3481-23352",
                "fax: (030)23451356"),
            df.createCustomer(" Tim ", " Schulz-Mueller ", "tim2346@gmx.de"),
            df.createCustomer("Nadine-Ulla Blumenfeld", "+49 152-92454"),
            df.createCustomer("Khaled Saad Mohamed Abdelalim", "+49 1524-12948210")
            // 
            // attempts to create Customer objects from invalid email address, no object is created
            df.createCustomer("Mandy Mondschein", "locomandy<>gmx.de"),
            df.createCustomer("", "nobody@gmx.de") // invalid name, no object is created
        // 
        ).stream()
            .flatMap(Optional::stream)
            .toList();

        // print numbers of created objects in respective collections
        System.out.println(String.format(
            "(%d) Customer objects built.\n" +
            "(%d) Article objects built.\n" +
            "(%d) Order objects built.\n---",
            customers.spliterator().getExactSizeIfKnown(), 0, 0));

        // print Customer table
        StringBuilder sb = printCustomers(customers);
        System.out.println(sb.insert(0, "Kunden:\n").toString());
    }
}
```

Running the code shows that invalid arguments (invalid email:
`"locomandy<>gmx.de"` and empty name `""`) do not create *Customer* objects
that are hence missing in the customer table:

```
Hello, se1.bestellsystem (Application_D1, modular)
(5) Customer objects built.
(0) Article objects built.
(0) Order objects built.
---
Kunden:
+----------+---------------------------------+---------------------------------+
| Kund.-ID | Name                            | Kontakt                         |
+----------+---------------------------------+---------------------------------+
|   892474 | Meyer, Eric                     | eric98@yahoo.com, (+2 contacts) |
|   643270 | Bayer, Anne                     | anne24@yahoo.de, (+2 contacts)  |
|   286516 | Schulz-Mueller, Tim             | tim2346@gmx.de                  |
|   412396 | Blumenfeld, Nadine-Ulla         | +49 152-92454                   |
|   456454 | Abdelalim, Khaled Saad Mohamed  | +49 1524-12948210               |
+----------+---------------------------------+---------------------------------+
```

Correct arguments and print the full customer list:

```
Hello, se1.bestellsystem (Application_D1, modular)
(7) Customer objects built.     <-- (7) Customers
(0) Article objects built.
(0) Order objects built.
---
Kunden:
+----------+---------------------------------+---------------------------------+
| Kund.-ID | Name                            | Kontakt                         |
+----------+---------------------------------+---------------------------------+
|   892474 | Meyer, Eric                     | eric98@yahoo.com, (+2 contacts) |
|   643270 | Bayer, Anne                     | anne24@yahoo.de, (+2 contacts)  |
|   286516 | Schulz-Mueller, Tim             | tim2346@gmx.de                  |
|   412396 | Blumenfeld, Nadine-Ulla         | +49 152-92454                   |
|   456454 | Abdelalim, Khaled Saad Mohamed  | +49 1524-12948210               |
|   651286 | Mondschein, Mandy               | locomandy@gmx.de                | <-- added
|   815160 | Meissner, Susanne               | nobody@gmx.de                   | <-- added
+----------+---------------------------------+---------------------------------+
```


<!-- 
&nbsp;

## 5. Update JUnit-Tests

*Customer* JUnit Tests no longer compile with the *DataFactory* changes.

Replace tests in: `tests/datamodel`. Download file:
[*c4-datafactory-tests.tar*](https://github.com/sgra64/se1-bestellsystem/blob/markup/c4-datafactory)
to the project directory and replace tests:

```sh
rm -rf tests/datamodel              # remove old tests
tar xvf c4-datafactory-tests.tar    # install new tests
```

Tests compile and run with the new code:

```sh
mk compile compile-tests run-tests
```
```
Test run finished after 486 ms
[        62 tests successful      ]
[         0 tests failed          ]
``` -->


&nbsp;

## 5. Commit and Push Changes

With all tests passing, commit and push changes to your remote repository.

```sh
git commit -m "c4: DataFactory, immutable Customer class, tests update"
git push                        # push new commit to your upstream remote repository
```
