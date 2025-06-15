# C2: *Customer.java*

Steps:

1. [Branch Assembly](#1-branch-assembly)

1. [Supplement Class *Customer.java* with *Javadoc*](#2-supplement-class-customerjava-with-javadoc)

1. [Implement Class *Customer.java*](#3-implement-class-customerjava)

1. [Push Commits to your Remote Repository](#4-push-commits-to-your-remote-repository)



&nbsp;

## 1. Branch Assembly

Tag the latest commit on branch `main` with `"base"`.

Create a new branch: `c12-customer` to implement the class: *Customer.java*.

Set a new remote with name: `se1-remote` pointing to the remote repository
from where the branch will be pulled:

```sh
git remote add se1-remote https://github.com/sgra64/se1-bestellsystem.git

git remote -v                   # show all remotes
```

Output will show two remotes: *"origin"* pointing to your remote repository
and the new remote: *"se1-remote"* :

```
origin  git@gitlab.bht-berlin.de:<your-name>/se1-bestellsystem.git (fetch)
origin  git@gitlab.bht-berlin.de:<your-name>/se1-bestellsystem.git (push)
se1-remote      https://github.com/sgra64/se1-bestellsystem.git (fetch)
se1-remote      https://github.com/sgra64/se1-bestellsystem.git (push)
```

Pull remote branch `se1-remote/c12-customer` into the new local
branch `c12-customer`:

```sh
# pull the remote branch (pull: fetch + merge), --strategy-option theirs
git pull se1-remote c12-customer \
    --squash --allow-unrelated-histories
```

Commit the changes that arrived with the pull:

```sh
git commit -m "pull se1-remote c12-customer"
```

A new class [*Application_C12.java*](src/application/Application_C12.java)
has been added to package `application`, which creates and prints some
*Customer* objects:

```java
/**
 * Run application code on an instance rather than in {@code static main();}
 */
@Override
public void run(String[] args) {
    // 
    final Customer eric = new Customer("Eric Meyer")
        .setId(892474L)     // set id, first time
        .setId(947L)        // ignored, since id can only be set once
        .addContact("eric98@yahoo.com")
        .addContact("eric98@yahoo.com") // ignore duplicate contact
        .addContact("(030) 3945-642298");

    final Customer anne = new Customer("Bayer, Anne")
        .setId(643270L)
        .addContact("anne24@yahoo.de")
        .addContact("(030) 3481-23352")
        .addContact("fax: (030)23451356");

    final Customer tim = new Customer("Tim Schulz-Mueller")
        .setId(286516L)
        .addContact("tim2346@gmx.de");

    final Customer nadine = new Customer("Nadine-Ulla Blumenfeld")
        .setId(412396L)
        .addContact("+49 152-92454");

    final Customer khaled = new Customer()
        .setName("Khaled Saad Mohamed Abdelalim")
        .setId(456454L)
        .addContact("+49 1524-12948210");

    final TableFormatter tf = new TableFormatter("|%-6s", "| %-32s", "| %-32s |")
        .line()
        .row("ID", "NAME", "CONTACTS")  // table header
        .line();

    final List<Customer> customers = List.of(eric, anne, tim, nadine, khaled);
}
```

Compile and run the code:

```sh
mk compile run                      # compile and run the code
```

Output shows an empty table since class *Customer* is still incomplete:

```
Hello, se1.bestellsystem (Application_C12, modular)
+------+---------------------------------+----------------------------------+
|ID    | NAME                            | CONTACTS                         |
+------+---------------------------------+----------------------------------+
|0     | ,                               |                                  |
|0     | ,                               |                                  |
|0     | ,                               |                                  |
|0     | ,                               |                                  |
|0     | ,                               |                                  |
+------+---------------------------------+----------------------------------+
```



&nbsp;

## 2. Supplement Class *Customer.java* with *Javadoc*

Create class `Customer.java` in package `datamodel` from the *UML Class Diagram:* 

<img src="https://raw.githubusercontent.com/sgra64/se1-bestellsystem/refs/heads/markup/main/Class-customer.png" alt="drawing" width="400"/>

Follow steps:

1. Create an empty class `Customer.java` in package `datamodel`.

1. Add *attributes*, *constructors* and *methods* as specified.

1. Add [*Javadoc*](https://en.wikipedia.org/wiki/Javadoc) from the documentation
    of class [*Customer.java*](https://sgra64.github.io/se1-bestellsystem/c12-customer)
    to understand methods and their expected behavior.

Understand particularly the purpose of method
[*splitName(String name)*](https://sgra64.github.io/se1-bestellsystem/c12-customer/se1.bestellsystem/datamodel/Customer.html#splitName(java.lang.String))
that splits a single-String name, e.g. "Meyer, Eric" into its first and last name parts.

Rebuild *Javadoc* locally:

```sh
mk javadoc                      # build Javadoc for the project
```
```
javadoc -d docs $(eval echo $JDK_JAVADOC_OPTIONS) \
  $(builtin cd src; find . -type d | sed -e 's!/!.!g' -e 's/^[.]*//')
Loading source files for package application...
Loading source files for package datamodel...
Constructing Javadoc information...
Creating destination directory: "docs\"
Building index for all the packages and classes...
Standard Doclet version 21+35-LTS-2513
Building tree for all the packages and classes...
Generating docs\se1.bestellsystem\application\Application.html...
Generating docs\se1.bestellsystem\application\Application_C12.html...
Generating docs\se1.bestellsystem\datamodel\Customer.html...
Generating docs\se1.bestellsystem\application\Runner.html...
Generating docs\se1.bestellsystem\application\package-summary.html...
Generating docs\se1.bestellsystem\application\package-tree.html...
Generating docs\se1.bestellsystem\datamodel\package-summary.html...
Generating docs\se1.bestellsystem\datamodel\package-tree.html...
Generating docs\se1.bestellsystem\module-summary.html...
Generating docs\overview-tree.html...
Building index for all classes...
Generating docs\allclasses-index.html...
Generating docs\allpackages-index.html...
Generating docs\index-all.html...
Generating docs\search.html...
Generating docs\index.html...
Generating docs\help-doc.html...
```

Fix errors and warnings when *Javadoc* compiles.

Open file: `docs/index.html` in a browser, navigate to class *Customer.java* and
compare with the *Javadoc* in
[*Customer.java*](https://sgra64.github.io/se1-bestellsystem/c12-customer/se1.bestellsystem/datamodel/Customer.html)

Both should be identical, except for the `Autor:` tag, which should show your name.



&nbsp;

## 3. Implement Class *Customer.java*

Implement methods according to the *Javadoc* specifications.
Pay attention return values and behavior for illegal arguments.

After implementation, re-compile and run the code. The table now shows the
created *Customer* objects:

```sh
mk compile run                      # compile and run the code
```
```
Hello, se1.bestellsystem (Application_C12, modular)
+------+---------------------------------+----------------------------------+
|ID    | NAME                            | CONTACTS                         |
+------+---------------------------------+----------------------------------+
|892474| Meyer, Eric                     | eric98@yahoo.com, (+1 contacts)  |
|643270| Bayer, Anne                     | anne24@yahoo.de, (+2 contacts)   |
|286516| Schulz-Mueller, Tim             | tim2346@gmx.de                   |
|412396| Blumenfeld, Nadine-Ulla         | +49 152-92454                    |
|456454| Abdelalim, Khaled Saad Mohamed  | +49 1524-12948210                |
+------+---------------------------------+----------------------------------+
```

Commit changes under `c12: Customer.java`.

Show commit log:

```sh
git log --oneline                   # show commit log
```
```
340e59f (HEAD -> main) c12: Customer.java       <-- latest commit
b1e8439 pull se1-remote c12-customer
aad5b4a add .gitmodules file and modules .env, .vscode, libs
44cdf9f add README.md
c43f7e1 add src, tests, resources
638c570 add .gitmodules, .gitmodules.sh
d8f0873 add .gitignore
4375dd3 (tag: root) root commit (empty)
```



&nbsp;

## 4. Push Commits to your Remote Repository

When tests are passing and Javadoc has been sucessfully created:

1. commit changes on your local branch `c12-customer`.

1. Push the branch to your remote repository.

1. Merge branch `c12-customer` to your local `main` branch.

1. Push branch `main` also to your remote repository.
