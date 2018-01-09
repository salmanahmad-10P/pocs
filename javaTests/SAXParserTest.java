import java.util.ArrayList;
import com.rfidgs.sofsa.utilities.*;

public class SAXParserTest {
	public static void main(String[] args)
	{
		try
		{
			// String testString = "<gold
			// xmlns=\"\"><resultset><result><po>0T4004</po><po_line>1</po_line><part>6640015007717</part><nsn>6665-01-X01-0736</nsn><noun>CARTRIDGE,
			// RESPIRATOR</noun><manuf_cage>1JDRO</manuf_cage><sc>ASL/P1</sc><qty_ordered></qty_ordered><qty_completed>20</qty_completed><um_show_code>EA</um_show_code><um_issue_code>EA</um_issue_code><um_turn_code>EA</um_turn_code><um_disp_code>EA</um_disp_code><um_cap_code>EA</um_cap_code><um_mil_code>EA</um_mil_code><unit_price>12.85</unit_price><condition>CAP-ORDER</condition><vendor_code></vendor_code><furnished_by>X</furnished_by></result></resultset></gold>";
			ReceiptInquiryParser parserObj = new ReceiptInquiryParser();
			ArrayList list = parserObj.createSPEAR_Tag_AddOn_List(testString);

			String testString = "<gold><resultset part="6640015007717"><result><nsn>6665-01-X01-0736</nsn><part>6640015007717</part><location>AR02A19</location><sc>CERF</sc><qty>4387</qty></result><result><nsn>6665-01-X01-0736</nsn><part>6640015007717</part><location>AR02A31</location><sc>CERF</sc><qty>880</qty></result><result><nsn>6665-01-X01-0736</nsn><part>6640015007717</part><location>BR01B12</location><sc>ASL/P1</sc><qty>180</qty></result></resultset></gold>";
			InventoryInquiryParser parserObj = new InventoryInquiryParser();
			ArrayList list = parserObj.createSPEAR_Tag_AddOn_List(testString);
		}
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
}
