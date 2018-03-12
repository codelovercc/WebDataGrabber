import org.junit.Test;
import org.springframework.util.NumberUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by codelover on 18/3/9.
 */

public class Tester {

    @Test
    public void testRegx(){
        //奇怪问题，这里的正则正常解析出数据，而在AmazonClothingProductInfoGrabberBehavior.java中125行，确只能解析出一个结果
        String rankOfFirstKind = "Amazon Best Sellers Rank: #165 in Clothing, Shoes & Jewelry (See Top 100 in Clothing, Shoes & Jewelry) " +
                "#3 in Clothing, Shoes & Jewelry > Women > Clothing > Socks & Hosiery > No Show & Liner Socks " +
                "#4 in Clothing, Shoes & Jewelry > Women > Shops > Plus-Size > Socks & Hosiery";
        rankOfFirstKind = rankOfFirstKind.replace('#', '\n');
        Matcher matcher = Pattern.compile("(\\d+[\\w\\s\\W]+)\\n?", Pattern.MULTILINE)
                .matcher(rankOfFirstKind);

        while (matcher.find()){
            System.out.println(matcher.group());
        }
    }

    @Test
    public void testDateFormat() throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        System.out.println(dateFormat.parse("May 23, 2017"));
        System.out.println(dateFormat.format(new Date()));;
    }

    @Test
    public void testParse() throws ParseException {

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        System.out.println(numberFormat.parse("           2,800.9     ".trim()));
        NumberFormat numberFormat1 = NumberFormat.getNumberInstance();
        System.out.println(numberFormat1.parse("2,899.91"));
        System.out.println(NumberUtils.parseNumber("2,899,32", Double.class, numberFormat));
        System.out.println("  $18.98 - $20.98  ".trim());
    }
}
