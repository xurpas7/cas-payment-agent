
import java.sql.Date;
import org.rmj.appdriver.GProperty;
import org.rmj.appdriver.GRider;
import org.rmj.payment.agent.XMSalesPayment;
import org.rmj.payment.agent.constant.PaymentType;

public class testSalesPayment {
    public static void main(String [] args){
        String lsProdctID = "gRider";
        String lsUserIDxx = "M001111122";
        GRider poGRider = new GRider(lsProdctID);
        GProperty loProp = new GProperty("GhostRiderXP");
        if (!poGRider.loadEnv(lsProdctID)) System.exit(0);
        if (!poGRider.logUser(lsProdctID, lsUserIDxx)) System.exit(0);
        
        XMSalesPayment instance = new XMSalesPayment(poGRider, poGRider.getBranchCode(), true);
        
        //test for check payment
        /*
        poGRider.beginTrans();
        instance.newTransaction(PaymentType.CHECK);
        instance.setMaster("sSourceCd", "ORec");
        instance.setMaster("sSourceNo", "M0011800000X");
        
        instance.setPayInfo("dTransact", Date.valueOf("2018-09-08"));
        instance.setPayInfo("sBankCode", "XXXXXXXXX");
        instance.setPayInfo("sCheckNox", "XXXX");
        instance.setPayInfo("dCheckDte", Date.valueOf("2018-09-08"));
        instance.setPayInfo("nAmountxx", 10000);
        instance.setPayInfo("sRemarksx", "this is a test");
        if (instance.saveUpdate())        
            poGRider.commitTrans();
        else poGRider.rollbackTrans();
        */
        
        /*
        if (instance.loadTransaction("M00118000009", "")){
            System.out.println(instance.getMaster("sSourceCd"));
            System.out.println(instance.getMaster("sSourceNo"));
            System.out.println(String.valueOf(instance.getMaster("sAmountxx")));
            
            System.out.println(String.valueOf(instance.getPayInfo("dTransact")));
            System.out.println(instance.getPayInfo("sBankCode"));
            System.out.println(instance.getPayInfo("sCheckNox"));
            System.out.println(String.valueOf(instance.getPayInfo("dCheckDte")));
            System.out.println(String.valueOf(instance.getPayInfo("nAmountxx")));
            System.out.println(instance.getPayInfo("sRemarksx"));
        }
        */
        
        //test for credit card payment
        /*
        poGRider.beginTrans();
        instance.newTransaction(PaymentType.CREDIT_CARD);
        instance.setMaster("sSourceCd", "ORec");
        instance.setMaster("sSourceNo", "M0011800000X");
        
        instance.setPayInfo("sBranchCd", "XXXX");
        instance.setPayInfo("sTermnlID", "MXXX");
        instance.setPayInfo("sBankCode", "XXXXXXXXX");
        instance.setPayInfo("sCardNoxx", "XXXX");
        instance.setPayInfo("sApprovNo", "XXXX");
        instance.setPayInfo("nAmountxx", 500);
        instance.setPayInfo("sTermCode", "XXXXXXX");
        if (instance.saveUpdate())        
            poGRider.commitTrans();
        else poGRider.rollbackTrans();
        */
        
        /*
        if (instance.loadTransaction("M00118000010", "")){
            System.out.println(instance.getMaster("sSourceCd"));
            System.out.println(instance.getMaster("sSourceNo"));
            System.out.println(String.valueOf(instance.getMaster("sAmountxx")));
            
            System.out.println(instance.getPayInfo("sBranchCd"));
            System.out.println(instance.getPayInfo("sTermnlID"));
            System.out.println(instance.getPayInfo("sBankCode"));
            System.out.println(instance.getPayInfo("sCardNoxx"));
            System.out.println(instance.getPayInfo("sApprovNo"));
            System.out.println(String.valueOf(instance.getPayInfo("nAmountxx")));
            System.out.println(instance.getPayInfo("sTermCode"));
        }
        */
        
        //test for credit card payment
        /*
        poGRider.beginTrans();
        instance.newTransaction(PaymentType.GIFT_CERTIFICATE);
        instance.setMaster("sSourceCd", "ORec");
        instance.setMaster("sSourceNo", "M0011800000X");
        
        instance.setPayInfo("sCompnyCd", "GMC");
        instance.setPayInfo("dValidity", Date.valueOf("2019-09-06"));
        instance.setPayInfo("sReferNox", "012345");
        instance.setPayInfo("nAmountxx", 10000);
        instance.setPayInfo("nAmtPaidx", 10000);
        instance.setPayInfo("sRemarksx", "this is a test");
        instance.setPayInfo("sSourceCd", "SO");
        instance.setPayInfo("sSourceNo", "M00118000001");
        if (instance.saveUpdate())        
            poGRider.commitTrans();
        else poGRider.rollbackTrans();
        */
        
        /*
        if (instance.loadTransaction("M00118000011", "")){
            System.out.println(instance.getMaster("sSourceCd"));
            System.out.println(instance.getMaster("sSourceNo"));
            System.out.println(String.valueOf(instance.getMaster("sAmountxx")));
            
            System.out.println(instance.getPayInfo("sCompnyCd"));
            System.out.println(String.valueOf(instance.getPayInfo("dValidity")));
            System.out.println(instance.getPayInfo("sReferNox"));
            System.out.println(String.valueOf(instance.getPayInfo("nAmountxx")));
            System.out.println(String.valueOf(instance.getPayInfo("nAmtPaidx")));
            System.out.println(instance.getPayInfo("sRemarksx"));
        }
        */
    }
}
