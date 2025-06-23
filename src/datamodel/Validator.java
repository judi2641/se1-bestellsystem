package datamodel;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;


/**
 * Verify input for valid <i>name</i> or <i>contact</i> specifications and
 * return validated results. Class is used by {@link DataFactory} methods
 * to create objects.
 * 
 * @version <code style=color:green>{@value application.package_info#Version}</code>
 * @author <code style=color:blue>{@value application.package_info#Author}</code>
 */
public final class Validator {

    /*
     * {@link Pattern} (regular) expressions validate String input, see
     * https://stackoverflow.com/questions/8204680/java-regex-email
     * 
     * A valid last name must start with an upper case letter followed
     * by upper or lower case letters, "-", "." or white spaces.
     * Valid last names "Meyer" or "von-Blumenfeld".
     * 
     * A valid first name can be empty or start with an upper case letter
     * followed by letters, "-", "." or white spaces. Valid first names
     * are "Eric", "Ulla-Nadine", but also "E", "E.".
     * 
     * Names do not include numbers, special or none-alphanumeric characters.
     */
    private final Pattern lastNamePattern = Pattern.compile("^[A-Z][A-Za-z-\\s]*");
    private final Pattern firstNamePattern = Pattern.compile("^$|^[A-Z][A-Za-z-\\s.]*");

    private final BiFunction<String, String, Boolean> validateNameParts =
        (first, last) -> last != null && first != null &&
            lastNamePattern.matcher(last).find() && firstNamePattern.matcher(first).find();

    /**
     * {@link Pattern} (regular) expression to validate an email address.
     */
    private final Pattern emailPattern =
        Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z0-9_]+$", Pattern.CASE_INSENSITIVE);

    /**
     * {@link Pattern} (regular) expression to validate a phone or fax number.
     */
    private final Pattern phonePattern =
        Pattern.compile("^(phone:|fax:|\\+[0-9]+){0,1}\\s*[\\s0-9()][\\s0-9()-]*", Pattern.CASE_INSENSITIVE);


    /*
     * Record to store first and last name parts.
     */
    public record NameParts(String firstName, String lastName) { };

    /**
     * Split <i>single-String</i> name into first and last name parts,
     * validate parts and return result as {@link NameParts} structure.
     * 
     * Rules to split a <i>single-String</i> name into first- and last name
     * parts are:
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
     * @param name <i>single-String</i> name to split into first and last name parts
     * @return valid {@link NameParts} or empty {@link Optional}
     */
    public Optional<NameParts> validateName(String name) {
        // 
        return Optional.ofNullable(name).map(n -> {
            String first="", last="";
            String[] split = n.split("[,;]");
            if(split.length > 1) {
                last=split[0]; first=split[1];
            } else {
                // if no separator [,;] -> split by white spaces:
                for(String s : name.split("\\s+")) {
                    if( last.length() > 0 ) {
                        // collect firstNames as found and lastName last
                        first += (first.length()==0? "" : " ") + last;
                    }
                    last = s;
                }
            }
            first=trim(first); last=trim(last);
            // 
            return validateNameParts.apply(first, last)? new NameParts(first, last) : null;
        }).filter(np -> np != null);
    }

    /**
     * Verify input for syntactically valid <i>email</i> address or
     * <i>phone</i> number.
     * @param contact contact to verify
     * @return valid contact or empty {@link Optional}
     */
    public Optional<String> validateContact(String contact) {
        return Optional.ofNullable(contact)
            .map(c -> c.trim())
            .filter(c -> contact != null && emailPattern.matcher(c).find()
                                         || phonePattern.matcher(c).find());
    }

    /**
     * Update {@link Customer} first and last name attributes from validated
     * single-String argument.
     * @param customer {@link Customer} object to update first and last name parts
     * @param name <i>single-String</i> name to update first and last name parts
     * @return reference to {@link Customer} object with updated names
     * @throws IllegalArgumentException if customer argument is null
     */
    public Customer updateName(Customer customer, String name) {
        if(customer==null)
            throw new IllegalArgumentException("customer is null");
        // 
        validateName(name)
            .ifPresent(np -> customer.nameUpdater.accept(np.firstName, np.lastName));
        // 
        return customer;
    }

    /**
     * Add validated contact to {@link Customer} object.
     * @param customer {@link Customer} object to add contact
     * @param contact contact to add
     * @return true if contact was added
     * @throws IllegalArgumentException if customer argument is null
     */
    public boolean addContact(Customer customer, String contact) {
        if(customer==null)
            throw new IllegalArgumentException("customer is null");
        // 
        return validateContact(contact)
            .map(c -> customer.contacts.add(contact)).isPresent();
    }

    /**
     * Remove <i>i-th</i> contact from {@link Customer} object.
     * @param customer {@link Customer} object to remove contact
     * @param i <i>i-th</i> contact to remove
     * @return true if contact was removed
     * @throws IllegalArgumentException if customer argument is null
     */
    public boolean removeContact(Customer customer, int i) {
        if(customer==null)
            throw new IllegalArgumentException("customer is null");
        // 
        boolean remove = i >= 0 && i < customer.contacts.size();
        if(remove) {
            customer.contacts.remove(i);
        }
        return remove;
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