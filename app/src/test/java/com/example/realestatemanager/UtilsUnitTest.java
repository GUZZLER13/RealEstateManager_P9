package com.example.realestatemanager;
import static org.junit.Assert.assertEquals;
import com.example.realestatemanager.utils.Utils;
import org.junit.Test;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class UtilsUnitTest {

    @Test
    public void checkConvertDollarToEuro() {
        assertEquals(89, Utils.convertDollarToEuro(100));
    }

    @Test
    public void checkConvertEuroToDollar() {
        assertEquals(112, Utils.convertEurosToDollars(100));
    }

    @Test
    public void checkIfGoodDateFormat() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse(Utils.getTodayDate());
        assert date != null;
        assertEquals(dateFormat.format(date), Utils.getTodayDate());
    }

    @Test
    public void checkIfGoodTimeMilis() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Long time = Utils.getDateInLong("12/02/2021");
        Date date = new Date(time);
        Date dateExcepted = dateFormat.parse("12/02/2021");
        assertEquals(dateExcepted, date);
    }
}
