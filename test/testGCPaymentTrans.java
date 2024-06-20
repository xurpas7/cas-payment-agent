import java.sql.Date;
import org.rmj.appdriver.GProperty;
import org.rmj.appdriver.GRider;
import org.rmj.payment.agent.XMGCPaymentTrans;

public class testGCPaymentTrans {
    public static void main(String [] args){
        String lsProdctID = "gRider";
        String lsUserIDxx = "M001111122";
        GRider poGRider = new GRider(lsProdctID);
        GProperty loProp = new GProperty("GhostRiderXP");
        if (!poGRider.loadEnv(lsProdctID)) System.exit(0);
        if (!poGRider.logUser(lsProdctID, lsUserIDxx)) System.exit(0);
        
        XMGCPaymentTrans instance = new XMGCPaymentTrans(poGRider, poGRider.getBranchCode(), false);
        /*
        instance.newTransaction();
        instance.setMaster("sCompnyCd", "GMC");
        instance.setMaster("dValidity", Date.valueOf("2019-09-06"));
        instance.setMaster("sReferNox", "012345");
        instance.setMaster("nAmountxx", 10000);
        instance.setMaster("nAmtPaidx", 10000);
        instance.setMaster("sRemarksx", "this is a test");
        instance.setMaster("sSourceCd", "SO");
        instance.setMaster("sSourceNo", "M00118000001");
        instance.saveUpdate();
        */
        /*
        instance.loadTransaction("M00118000002");
        System.out.println((String) instance.getMaster("sCompnyCd"));
        System.out.println(String.valueOf(instance.getMaster("dValidity")));
        System.out.println((String) instance.getMaster("sReferNox"));
        System.out.println(String.valueOf(instance.getMaster("nAmountxx")));
        System.out.println(String.valueOf(instance.getMaster("nAmtPaidx")));
        System.out.println((String) instance.getMaster("sRemarksx"));
        System.out.println((String) instance.getMaster("sSourceCd"));
        System.out.println((String) instance.getMaster("sSourceNo"));
        */

    }
}
