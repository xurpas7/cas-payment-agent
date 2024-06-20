import java.sql.Date;
import org.rmj.appdriver.GProperty;
import org.rmj.appdriver.GRider;
import org.rmj.payment.agent.XMCheckPaymentTrans;

public class testCheckPaymentTrans {
    public static void main(String [] args){
        String lsProdctID = "gRider";
        String lsUserIDxx = "M001111122";
        GRider poGRider = new GRider(lsProdctID);
        GProperty loProp = new GProperty("GhostRiderXP");
        if (!poGRider.loadEnv(lsProdctID)) System.exit(0);
        if (!poGRider.logUser(lsProdctID, lsUserIDxx)) System.exit(0);
        
        XMCheckPaymentTrans instance = new XMCheckPaymentTrans(poGRider, poGRider.getBranchCode(), false);
        /*
        instance.newTransaction();
        instance.setMaster("dTransact", Date.valueOf("2018-09-06"));
        instance.setMaster("sBankCode", "M00118001");
        instance.setMaster("sCheckNox", "7777");
        instance.setMaster("dCheckDte", Date.valueOf("2018-09-06"));
        instance.setMaster("nAmountxx", 10000);
        instance.setMaster("sRemarksx", "this is a test");
        instance.setMaster("sSourceCd", "SO");
        instance.setMaster("sSourceNo", "M00118000001");
        instance.saveUpdate();
        */
        
        instance.loadTransaction("M00117000018", "SPmt");
        System.out.println(String.valueOf(instance.getMaster("dTransact")));
        System.out.println((String) instance.getMaster("sBankCode"));
        System.out.println((String) instance.getMaster("sCheckNox"));
        System.out.println(String.valueOf(instance.getMaster("dCheckDte")));
        System.out.println(String.valueOf(instance.getMaster("nAmountxx")));
        System.out.println((String) instance.getMaster("sRemarksx"));
        System.out.println((String) instance.getMaster("sSourceCd"));
        System.out.println((String) instance.getMaster("sSourceNo"));
        
    }
}
