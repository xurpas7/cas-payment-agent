/**
 * @author  Michael Cuison
 * 
 * @since    2019-06-10
 */

package org.rmj.payment.agent.iface;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CashPulloutFX extends Application {
    public final static String pxeMainForm = "cashpullout.fxml";
    public final static String pxeStageIcon = "org/rmj/appdriver/agentfx/styles/64.png";
    public final static String pxeMainFormTitle = "System Approval";
    
    private double xOffset = 0; 
    private double yOffset = 0;
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(pxeMainForm));
        
        //get the controller of the main interface
        CashPulloutController loControl = new CashPulloutController();
        
        loControl.setOpeningBalance(nOpenAmtx);
        loControl.setCashAmount(nCashAmtx);
        loControl.setCheckAmount(nCheckAmt);
        loControl.setChargeAmount(nChargexx);
        loControl.setCreditCardAmount(nCredtCrd);
        loControl.setGiftCertAmount(nGiftCert);
        loControl.setFinanceAmount(nFinAmntx);
        loControl.setWithrawal(nWithdraw);
        loControl.setDeposit(nDepositx);
        
        //the controller class to the main interface
        fxmlLoader.setController(loControl);
        
        //load the main interface
        Parent parent = fxmlLoader.load();
        
        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        
        //set the main interface as the scene
        Scene scene = new Scene(parent);
        
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(pxeStageIcon));
        stage.setTitle(pxeMainFormTitle);
        stage.showAndWait();
        
        if (!loControl.isCancelled()){
            nCPullout = loControl.getCashPullOut();
            bCancel = false;
        } else 
            bCancel = true;
    }

    public static void main(String[] args) {launch(args);}
    
    public void setOpeningBalance(Number fnValue){nOpenAmtx = fnValue;}
    public void setCashAmount(Number fnValue){nCashAmtx = fnValue;}
    public void setCheckAmount(Number fnValue){nCheckAmt = fnValue;}
    public void setChargeAmount(Number fnValue){nChargexx = fnValue;}
    public void setCreditCardAmount(Number fnValue){nCredtCrd = fnValue;}
    public void setGiftCertAmount(Number fnValue){nGiftCert = fnValue;}
    public void setFinanceAmount(Number fnValue){nFinAmntx = fnValue;}
    public void setWithrawal(Number fnValue){nWithdraw = fnValue;}
    public void setDeposit(Number fnValue){nDepositx = fnValue;}
    
    public Number getCashPullout(){return nCPullout;}
    public boolean isCancelled(){return bCancel;}
    
    public static Number nOpenAmtx = 0.00;
    public static Number nCashAmtx = 0.00;
    public static Number nCheckAmt = 0.00;
    public static Number nChargexx = 0.00;
    public static Number nCredtCrd = 0.00;
    public static Number nGiftCert = 0.00;
    public static Number nFinAmntx = 0.00;
    public static Number nWithdraw = 0.00;
    public static Number nDepositx = 0.00;
    public static Number nCPullout = 0.00;
    public static boolean bCancel = true;
}
