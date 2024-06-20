
import org.rmj.appdriver.GProperty;
import org.rmj.appdriver.GRider;
import org.rmj.payment.agent.XMCreditCardTrans;

public class testCreditCardTrans {
    public static void main(String [] args){
        String lsProdctID = "gRider";
        String lsUserIDxx = "M001111122";
        GRider poGRider = new GRider(lsProdctID);
        GProperty loProp = new GProperty("GhostRiderXP");
        if (!poGRider.loadEnv(lsProdctID)) System.exit(0);
        if (!poGRider.logUser(lsProdctID, lsUserIDxx)) System.exit(0);
        
        XMCreditCardTrans instance = new XMCreditCardTrans(poGRider, poGRider.getBranchCode(), false);
        
        /*
        instance.newTransaction();
        instance.setMaster("sBranchCd", "M001");
        instance.setMaster("sTermnlID", "MXXX");
        instance.setMaster("sBankCode", "M00118001");
        instance.setMaster("sCardNoxx", "7777");
        instance.setMaster("sApprovNo", "1234");
        instance.setMaster("nAmountxx", 10000);
        instance.setMaster("sTermCode", "M001001");
        instance.setMaster("sSourceCd", "SO");
        instance.setMaster("sSourceNo", "M00118000001");
        instance.saveUpdate();
        */
        /*
        instance.loadTransaction("M00118000003");
        System.out.println((String) instance.getMaster("sBranchCd"));
        System.out.println((String) instance.getMaster("sTermnlID"));
        System.out.println((String) instance.getMaster("sBankCode"));
        System.out.println((String) instance.getMaster("sCardNoxx"));
        System.out.println((String) instance.getMaster("sApprovNo"));
        System.out.println(String.valueOf(instance.getMaster("nAmountxx")));
        System.out.println((String) instance.getMaster("sTermCode"));
        System.out.println((String) instance.getMaster("sSourceCd"));
        System.out.println((String) instance.getMaster("sSourceNo"));
        */
    }
}
