package application;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import datamodel.Customer;
import datamodel.DataFactory;


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
            df.createCustomer("Eric", "Meyer", "eric98@yahoo.com", "eric98@yahoo.com", "(030) 3945-642298"),
            df.createCustomer("Bayer, Anne", "anne24@yahoo.de", "(030) 3481-23352", "fax: (030)23451356"),
            df.createCustomer(" Tim ", " Schulz-Mueller ", "tim2346@gmx.de"),
            df.createCustomer("Nadine-Ulla Blumenfeld", "+49 152-92454"),
            df.createCustomer("Khaled Saad Mohamed Abdelalim", "+49 1524-12948210"),
            // 
            // attempts to create Customer objects from invalid email address, no object is created
            df.createCustomer("Mandy Mondschein", "locomandy@gmx.de"),
            df.createCustomer("Susanne Meissner", "nobody@gmx.de") // invalid name, no object is created
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

    /**
     * Print objects of class {@link Customer} as table row into a {@link StringBuilder}.
     * @param customers customer objects to print
     * @return StringBuilder with customers rendered as rows in table format
     * @throws IllegalArgumentException with null arguments
     */
    public StringBuilder printCustomers(Collection<Customer> customers) {
        if(customers==null)
            throw new IllegalArgumentException("argument customers: null");
        //
        final TableFormatter tf = new TableFormatter(
                // table column specification
                "| %8s ", "| %-32s", "| %-31s |")
            .line()
            .row("Kund.-ID", "Name", "Kontakt") // table header
            .line();
        //
        // print {@link Customer} rows:
        customers.stream()
            // @REMOVE
            // .sorted((c1, c2) -> c1.getLastName().compareTo(c2.getLastName()))
            // @/REMOVE
            .forEach(c -> {
                var id = String.format("%d", c.id());
                var name = fmtCustomerName(c);
                var contact = fmtCustomerContacts(c, 1);
                //
                tf.row(id, name, contact);  // write row into table
            });
        return tf.line().get();
    }

    /**
     * Format Customer name according to a format (0 is default):
     * <pre>
     * fmt: 0: "Meyer, Eric"  10: "MEYER, ERIC"
     *      1: "Eric Meyer"   11: "ERIC MEYER"
     *      2: "Meyer, E."    12: "MEYER, E."
     *      3: "E. Meyer"     13: "E. MEYER"
     *      4: "Meyer"        14: "MEYER"
     *      5: "Eric"         15: "ERIC"
     * </pre>
     * 
     * @param customer Customer object.
     * @param fmt name formatting style.
     * @return formatted Customer name.
     */
    public String fmtCustomerName(final Customer customer, final int... fmt) {
        if(customer==null)
            throw new IllegalArgumentException("Customer null.");
        //
        String ln = customer.lastName();
        String fn = customer.firstName();
        String fn1 = fn.length() > 0? fn.substring(0, 1).toUpperCase() : "";
        //
        final int ft = fmt.length > 0? fmt[0] : 0;  // 0 is default format
        switch(ft) {    // 0 is default
        case 0: return String.format("%s, %s", ln, fn);
        case 1: return String.format("%s %s", fn, ln);
        case 2: return String.format("%s, %s.", ln, fn1);
        case 3: return String.format("%s. %s", fn1, ln);
        case 4: return ln;
        case 5: return fn;
        //
        case 10: case 11: case 12: case 13: case 14: case 15:
            return fmtCustomerName(customer, ft - 10).toUpperCase();
        //
        default: return fmtCustomerName(customer, 0);
        }
    }

    /**
     * Format Customer contacts according to a format (0 is default):
     * <pre>
     * fmt: 0: first contact: "anne24@yahoo.de"
     *      1: first contact with extension indicator: "anne24@yahoo.de, (+2 contacts)"
     *      2: all contacts as list: "anne24@yahoo.de, (030) 3481-23352, fax: (030)23451356"
     * </pre>
     * 
     * @param customer Customer object.
     * @param fmt name formatting style.
     * @return formatted Customer contact information.
     */
    public String fmtCustomerContacts(final Customer customer, final int... fmt) {
        if(customer==null)
            throw new IllegalArgumentException("Customer null.");
        //
        var clen = customer.contactsCount();
        final int ft = fmt.length > 0? fmt[0] : 0;  // 0 is default format
        switch(ft) {    // 0 is default
        case 0:
            return String.format("%s", clen > 0? customer.contacts().iterator().next() : "");

        case 1:
            String ext = clen > 1? String.format(", (+%d contacts)", clen - 1) : "";
            return String.format("%s%s", fmtCustomerContacts(customer, 0), ext);

        case 2:
            StringBuilder sb = new StringBuilder();
            StreamSupport.stream(customer.contacts().spliterator(), false)
                .forEach(contact -> sb.append(contact).append(sb.length() > 0? ", " : ""));
            return sb.toString();
        //
        default: return fmtCustomerContacts(customer, 0);
        }
    }

    /**
     * Format long value to a decimal String with specified digit formatting:
     * <pre>
     *      {      "%,d", 1L },     // no decimal digits:  16,000Y
     *      { "%,d.%01d", 10L },
     *      { "%,d.%02d", 100L },   // double-digit price: 169.99E
     *      { "%,d.%03d", 1000L },  // triple-digit unit:  16.999-
     * </pre>
     * @param value value to format to String in decimal format
     * @param decimalDigits number of digits
     * @param unit appended unit as String
     * @return decimal value formatted according to specified digit formatting
     */
    public String fmtDecimal(long value, int decimalDigits, String... unit) {
        final String unitStr = unit.length > 0? unit[0] : null;
        final Object[][] dec = {
            {      "%,d", 1L },     // no decimal digits:  16,000Y
            { "%,d.%01d", 10L },
            { "%,d.%02d", 100L },   // double-digit price: 169.99E
            { "%,d.%03d", 1000L },  // triple-digit unit:  16.999-
        };
        String result;
        String fmt = (String)dec[decimalDigits][0];
        if(unitStr != null && unitStr.length() > 0) {
            fmt += "%s";	// add "%s" to format for unit string
        }
        int decdigs = Math.max(0, Math.min(dec.length - 1, decimalDigits));
        //
        if(decdigs==0) {
            Object[] args = {value, unitStr};
            result = String.format(fmt, args);
        } else {
            long digs = (long)dec[decdigs][1];
            long frac = Math.abs( value % digs );
            Object[] args = {value/digs, frac, unitStr};
            result = String.format(fmt, args);
        }
        return result;
    }

    /**
     * Class of a table formatter that uses String.format(fmt) to format cells.
     * 
     * @author sgra64
     *
     */
    class TableFormatter {

        /**
         * Format specifiers for each column.
         */
        private final List<String> fmts;

        /**
         * Width of each column.
         */
        private final List<Integer> widths;

        /**
         * Collect formatted rows.
         */
        private final StringBuilder sb;


        /**
         * Constructor with String.format(fmt) specifiers for each column.
         * 
         * @param fmtArgs String.format(fmt) specifiers for each column.
         */
        public TableFormatter(String... fmtArgs) {
            this((StringBuilder)null, fmtArgs);
        }

        /**
         * Constructor with external collector of table rows and String.format(fmt)
         * specifiers for each column.
         * 
         * @param sb external collector for table rows.
         * @param fmtArgs String.format(fmt) specifiers for each column.
         */
        public TableFormatter(StringBuilder sb, String... fmtArgs) {
            this.sb = sb != null? sb : new StringBuilder();
            this.fmts = Arrays.stream(fmtArgs).toList();
            this.widths = fmts.stream().map(fmt -> String.format(fmt, "").length()).toList();
        }

        /**
         * Add row to table. Each cell is formatted according to the column fmt specifier.
         * 
         * @param cells variable array of cells.
         * @return chainable self-reference.
         */
        public TableFormatter row(String... cells) {
            IntStream.range(0, Math.min(fmts.size(), cells.length)).forEach(i -> {
                sb.append(fillCell(i, cells[i], t -> {
                    String fmt = fmts.get(i);
                    int i1 = fmt.indexOf('%');  // offset width by format chars, e.g. '%-20s'
                    int i2 = Math.max(fmt.indexOf('s'), fmt.indexOf('d'));  // end '%s', '%d'
                    int offset = fmt.length() - (i2 - i1) -1;
                    // cut cell text to effective column width
                    t = t.substring(0, Math.min(t.length(), widths.get(i) - offset));
                    return String.format(fmt, t);
                }));
            });
            return this.endRow();
        }

        /**
         * Add line comprised of segments for each column to the table.
         * Segments are drawn based on segment spefifiers with:
         * <pre>
         * seg: null    - empty or blank segment
         *      ""      - segment filled with default character: "-"
         *      "="     - segment is filled with provided character.
         * </pre>
         * 
         * @param segs variable array of segment specifiers.
         * @return chainable self-reference.
         */
        public TableFormatter line(String... segs) {
            if(segs.length==0) {    // print full line when segs is empty
                String[] args = fmts.stream().map(f -> "").toArray(String[]::new);
                return line(args);  // invoke recursively with ""-args
            }
            IntStream.range(0, Math.min(fmts.size(), segs.length)).forEach(i -> {
                sb.append(fillCell(i, segs[i], s -> {
                    s = s.length() > 0? s.substring(0, 1) : "-"; // filler char
                    return String.format(fmts.get(i), "")
                            .replaceAll("[^\\|]", s).replaceAll("[\\|]", "+");
                }));
            });
            return this.endRow();
        }

        /**
         * Getter to collected table content.
         * 
         * @return table content.
         */
        public StringBuilder get() { return sb; }

        /*
         * private helper methods.
         */

        private String fillCell(int i, String text, Function<String, String> cellFiller) {
            return text != null? cellFiller.apply(text) : " ".repeat(widths.get(i));
        }

        private TableFormatter endRow() { sb.append("\n"); return this; }
    }
}