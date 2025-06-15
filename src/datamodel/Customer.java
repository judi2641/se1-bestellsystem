package datamodel;

import java.util.ArrayList;

public class Customer {
    private long id;
    private String firstName;
    private String lastName;
    private final ArrayList<String> contacts;

    public Customer() {
        this.id = -1;
        this.firstName = "";
        this.lastName = "";
        this.contacts = new ArrayList<>();
    }

    public Customer(String name) {
        if(name.isBlank() || name == null){
            throw new IllegalArgumentException("name argument is null or empty");
        }
        this.id = -1;
        splitName(name);
        this.contacts = new ArrayList<>();
    }


    public long getId() {
        if(id < 0){
            return -1;
        }
        else{
            return this.id;
        }
    }

    public Customer setId(long id) {
        if(this.id >= 0){
            return this;
        }
        if(id < 0){
            throw new IllegalArgumentException("id argument is not valid");
        }
        this.id = id;
        return this;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Customer setName(String first, String last) {
        if(first == null || first.isBlank()){
            throw new IllegalArgumentException("firstName argument is null or empty");
        }
        if(last == null || last.isBlank()){
            throw new IllegalArgumentException("lastName argument is null or empty");
        }
        this.firstName = first;
        this.lastName = last;
        return this;

    }

    public Customer setName(String name) {
        splitName(name);
        return this;
    }

    public int contactsCount() {
        return contacts.size();
    }

    public Iterable<String> getContacts() {
        return java.util.List.of(this.contacts.toArray(String[]::new));
    }

    public Customer addContact(String contact) {
        if(contact == null || contact.isBlank()){
            throw new IllegalArgumentException("contact argument is null or empty");
        }
        if(contact.trim().length() < 5){
            throw new IllegalArgumentException("contact argmuent must have at least 6 characters");
        }
        if(this.contacts.contains(contact)){
            return this;
        }
        this.contacts.add(contact);
        return this;
    }

    public void deleteContact(int i) {
        if(contactsCount() >= i || i > 0){
            this.contacts.remove(i);
        }
    }

    public void deleteAllContacts() {
        contacts.clear();;
    }

    /**
     * Split single-String name into last- and first name parts according to
     * rules:
     * <ul>
     * <li> if a name contains no seperators (comma or semicolon {@code [,;]}),
     *      the trailing consecutive part is the last name, all prior parts
     *      are first name parts, e.g. {@code "Tim Anton Schulz-Müller"}, splits
     *      into <i>first name:</i> {@code "Tim Anton"} and <i>last name</i>
     *      {@code "Schulz-Müller"}.
     * <li> names with seperators (comma or semicolon {@code [,;]}) split into
     *      a last name part before the seperator and a first name part after
     *      the seperator, e.g. {@code "Schulz-Müller, Tim Anton"} splits into
     *      <i>first name:</i> {@code "Tim Anton"} and <i>last name</i>
     *      {@code "Schulz-Müller"}.
     * <li> leading and trailing white spaces {@code [\s]}, commata {@code [,;]}
     *      and quotes {@code ["']} must be trimmed from names, e.g.
     *      {@code "  'Schulz-Müller, Tim Anton'    "}.
     * <li> interim white spaces between name parts must be trimmed, e.g.
     *      {@code "Schulz-Müller, <white-spaces> Tim <white-spaces> Anton <white-spaces> "}.
     * </ul>
     * <pre>
     * Examples:
     * +------------------------------------+-----------------------+-----------------------+
     * |Single-String name                  |first name parts       |last name parts        |
     * +------------------------------------+-----------------------+-----------------------+
     * |"Eric Meyer"                        |"Eric"                 |"Meyer"                |
     * |"Meyer, Anne"                       |"Anne"                 |"Meyer"                |
     * |"Meyer; Anne"                       |"Anne"                 |"Meyer"                |
     * |"Tim Schulz‐Mueller"                |"Tim"                  |"Schulz‐Mueller"       |
     * |"Nadine Ulla Blumenfeld"            |"Nadine Ulla"          |"Blumenfeld"           |
     * |"Nadine‐Ulla Blumenfeld"            |"Nadine‐Ulla"          |"Blumenfeld"           |
     * |"Khaled Saad Mohamed Abdelalim"     |"Khaled Saad Mohamed"  |"Abdelalim"            |
     * +------------------------------------+-----------------------+-----------------------+
     * 
     * Trim leading, trailing and interim white spaces and quotes:
     * +------------------------------------+-----------------------+-----------------------+
     * |" 'Eric Meyer'  "                   |"Eric"                 |"Meyer"                |
     * |"Nadine     Ulla     Blumenfeld"    |"Nadine Ulla"          |"Blumenfeld"           |
     * +------------------------------------+-----------------------+-----------------------+
     * </pre>
     * @param name single-String name to split into first- and last name parts
     * @throws IllegalArgumentException if name argument is null or empty
     */
    public void splitName(String name) {
        name = trim(name);
    
        if (name.contains(",") || name.contains(";")) {
            String[] parts = name.split("[,;]");
            this.lastName = trim(parts[0]).replaceAll("\\s+", " ");
            this.firstName = trim(parts[1]).replaceAll("\\s+", " ");
            return;
        }

        
        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            this.firstName = "";
            this.lastName = parts[0];
        } else {
            StringBuilder first = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                first.append(parts[i]).append(" ");
            }
            this.firstName = first.toString().trim().replaceAll("\\s+", " ");
            this.lastName = parts[parts.length - 1];
        }
    }

    /**
     * Trim leading and trailing white spaces {@code [\s]}, commata {@code [,;]}
     * and quotes {@code ["']} from a String (used for names and contacts).
     * @param s String to trim
     * @return trimmed String
     */
    private String trim(String s) {
        s = s.replaceAll("^[\\s\"',;]*", "");   // trim leading white spaces[\s], commata[,;] and quotes['"]
        s = s.replaceAll( "[\\s\"',;]*$", "");  // trim trailing accordingly
        return s;
    }
}