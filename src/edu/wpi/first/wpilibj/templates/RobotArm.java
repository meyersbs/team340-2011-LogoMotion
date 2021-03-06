/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Ben Meyers
 *********************************************************************
 * Banner Sensor on 2Kx has values 0 to 16
 * Banner Value -5      --> Ground Position
 * Banner Value -3      --> Bottom Position
 * Banner Value -1      --> Human Position
 * Banner Value  1      --> Middle Position
 * Banner Value  3      --> Top Position
 *********************************************************************
 */

public class RobotArm
{
    private RobotIO rIO;
    private HumanIO hIO;

    private static boolean prev = false;
    private Timer MyTimer;
    private static boolean start, done;
     private static double timeValue;

    /**
     * References to RobotIO and HumanIO so they can be used in RobotArm Class
     * @param rIO
     * @param hIO
     */
    public RobotArm(RobotIO rIO, HumanIO hIO)
    {
        this.rIO = rIO;
        this.hIO = hIO;
        MyTimer = new Timer();
        start = false;
        done = false;
    }


    /**
     * This allows the human to control the arm both manually and automagically
     *
     */
    public void humanControl()
    {
        this.armControl(hIO.getElevatorSwitchValue());
        //System.out.println("Arm Encoder Value:" + rIO.getArmEncoder());
        this.tilt();
        //System.out.println("Bottom Limit:" + rIO.getBottomLimitValue());
        if(rIO.getArmEncoder() == hIO.GROUND_VAL)
        {

        }
    }
    /**
     * Sets the arm motor value to the left joystick value
     */
    public void Manual()
    {
        //rIO.setArmMotorValue(hIO.getLeftJoyY());// correct joystick?
        //System.out.println("Driving");
    }
    /**
     * Checks banner sensor value and tells the arm what to do
     */

//    public void tilt()
//    {
//        System.out.print(" In Tilt");
//        if(rIO.getArmEncoder() <= (hIO.HUMAN_VAL - hIO.ARM_POS_DIF))
//        {
//            System.out.print(" tilt We're below the limit");
//            rIO.setArmTilt(!rIO.getArmTiltUpValue());
//        }
//        else
//        {
//            System.out.print(" dont tilt we're above the limit");
//            return;
//        }
//        return;
//    }
    
        public void tilt()
    {
        //System.out.print(" In Tilt");
        //System.out.println(" Arm Tilt:" + rIO.getArmTiltUpValue());
        if(true) //&& rIO.getArmEncoder() <= (hIO.HUMAN_VAL)) // Ben Meyers
        {
            if(hIO.armTiltSwitch == true)
            {
                //System.out.print(" Tilting Arm Down");
                rIO.setArmTilt(true);
            }

            else if(hIO.armTiltUpSwitch == true)
            {
                //System.out.println(" Tilting Arm Up");
                rIO.setArmTilt(false);
            }
        }
        else
        {
            //System.out.print(" dont tilt we're above the limit");
            return;
        }
        return;
    }

    /**
     * Tells the arm whether to go up, down or stop depending on where the arm currently is
     * @param pos
     */
    public boolean TimedGoUp(double goTime){
        if(start == false){
            MyTimer.reset();
            MyTimer.start();
            timeValue = goTime;
            rIO.setArmMotorValue(rIO.ARM_MOTOR_UP_VAL);
            done = false;
            start = true;
            System.out.println(" Going ");
        }
        else{
            if(MyTimer.get()>=timeValue){
                MyTimer.stop();
                MyTimer.reset();
                rIO.setArmMotorValue(rIO.ARM_MOTOR_OFF_VAL);
                done = true;
                start = false;
                System.out.println(" stop ");
            }
        }
        return done;
   }
    public boolean goToPos(int pos)
    {
        //rIO.getArmEncoder();
        //System.out.println("going to : " + pos + " from :" + rIO.getArmEncoder());
        if(rIO.getArmEncoder() > (pos+10)  && (rIO.getBottomLimitValue() == true))
         //If our encoder position is higher than our position and the bottom limit is not pressed, go down
        {
            this.goDown();
        }
        else if((rIO.getArmEncoder() < (pos-10) && !rIO.getArmTiltUpValue() && rIO.getArmEncoder() < (hIO.HUMAN_VAL)) ||
                (rIO.getArmEncoder() < (pos-10) && !rIO.getArmTiltUpValue() &&!(rIO.getArmEncoder() < (hIO.HUMAN_VAL))) ||
                (rIO.getArmEncoder() < (pos-10) &&  rIO.getArmTiltUpValue() && rIO.getArmEncoder() < (hIO.HUMAN_VAL)))
              //((rIO.getArmEncoder() < pos && !rIO.getArmTiltSensorVal() && rIO.getArmEncoder() < (hIO.HUMAN_VAL)) ||
              // (rIO.getArmEncoder() < pos && !rIO.getArmTiltSensorVal() &&!(rIO.getArmEncoder() < (hIO.HUMAN_VAL))) ||
              // (rIO.getArmEncoder() < pos &&  rIO.getArmTiltSensorVal() && rIO.getArmEncoder() < (hIO.HUMAN_VAL)))
            //Arm Safety: No movement above Human Level if tilted backwards
        {
            this.goUp();
        }
        else
        {
            this.stop();
            return true;
        }
        return false;
    }

    public void armControl(int armCommand)
    {
        System.out.print("Encoder Value1: " + rIO.getArmEncoder()+ "\n");
        //System.out.print("Encoder Value2: " + rIO.getArmEncoderNew()+ "\n");
        if(armCommand == hIO.ARM_STOP)
        {
            //System.out.println("human stop");
            this.stop();
        }
        else if(armCommand == hIO.ARM_UP)
        {
            //System.out.println("human up");
            this.goUp();
        }
        else if(armCommand == hIO.ARM_DOWN)
        {
            if(rIO.getBottomLimitValue()){
                this.goDown();
                //System.out.println("human down");
            }else{
                this.stop();
                //System.out.println("human down but lets not kill the arm");
            }
        }
        else if(armCommand == hIO.GROUND_VAL)
        {
            this.goToGround();
        }
        else if(armCommand <= hIO.TOP_VAL && armCommand > hIO.GROUND_VAL)
        {
            goToPos(armCommand);
            /*TODO: *****Uncomment above line Ben Meyers*****/
        }
        else
        {
            this.stop();
        }
    }


    /**
     * The following methods make the arm move to where the driver tells it to
     */
    /**
     * These methods set the arm motor to go up, down or stop
     */
    public boolean goToGround()
    {

        if(rIO.getBottomLimitValue() == false)
        {
            this.stop();
            System.out.println("at ground");
            return true;
            
        }
        else
        {
            this.goDown();
            System.out.println("going to to ground");
            return false;
            //
        }
    }
    public void goUp()
    {
        rIO.setArmMotorValue(rIO.ARM_MOTOR_UP_VAL);
        //System.out.println("Arm Motor Going Up");
    }
    public void goDown()
    {
        if(false)// && rIO.getArmEncoder() < hIO.BOT_VAL) //Did this because no encoder Ben Meyers
        {
            rIO.setArmMotorValue(rIO.ARM_MOTOR_DN_VAL/3);
        }
        else
        {
            rIO.setArmMotorValue(rIO.ARM_MOTOR_DN_VAL);
        }
        //System.out.println("Arm Motor Going Down");
    }
    private void stop()
    {
        rIO.setArmMotorValue(rIO.ARM_MOTOR_OFF_VAL);
        if(rIO.getBottomLimitValue() == false) // "false" in this instance means pressed
        {
            System.out.println("Arm Encoder Values Reset");
            rIO.armBannerSensorEncoder.reset();
            hIO.GROUND_VAL  = 0;
            //hIO.BOT_VAL     = 2;
            //hIO.HUMAN_VAL   = 4;
            //hIO.MID_VAL     = 6;
            //hIO.TOP_VAL     = 8;
            /**Ben Meyers
             * TODO: *****Change the above values*****
             */
        }
        //System.out.println("Arm Motor Stopped");

    }
}
/**public void Auto()
    {
        /**if(hIO.elevatorSwitchValue() == 0)
        {
            this.goToGround();
        }
        if(hIO.getElevatorSwitchValue() == hIO.BOT_VAL)
        {
            this.goToBotPosition();
        }
        else if(hIO.getElevatorSwitchValue() == hIO.MID_VAL)
        {
            this.goToMidPosition();
        }
        else if(hIO.getElevatorSwitchValue() == hIO.TOP_VAL)
        {
            this.goToTopPosition();
        }
        else if(hIO.getElevatorSwitchValue() == hIO.GROUND_VAL)
        {
            this.goToGround();
        }
        else if(hIO.getElevatorSwitchValue() == hIO.HUMAN_VAL)
        {
            this.goToHuman();
        }
        else
        {
            this.stop();
        }
        /**else if(hIO.getElevatorSwitchValue() == hIO.CTR_BOT_VAL)
        {
            this.goToCtrBottom();
        }
        else if(hIO.getElevatorSwitchValue() == hIO.CTR_MID_VAL)
        {
            this.goToCtrMid();
        }
        else if(hIO.getElevatorSwitchValue() == hIO.CTR_TOP_VAL)
        {
            this.goToCtrTop();
        }
        else if(hIO.elevatorSwitchValue() == 7)
        {
            this.ResetArm();
        }
    }*/

/*    public boolean goToBotPosition()
    {
        System.out.println("Going to Position 1:" + rIO.getArmEncoder());
        return this.goToPos(1);
    }
    public boolean goToMidPosition()
    {
        System.out.println("Going to Position 2:" + rIO.getArmEncoder());
        return this.goToPos(2);
    }
    public boolean goToTopPosition()
    {
        System.out.println("Going to Position 3:" + rIO.getArmEncoder());
        return this.goToPos(3);
    }
    public boolean goToGround()
    {
        System.out.println("Going to Position 0:" + rIO.getArmEncoder());
        return this.goToPos(0);
    }
    public boolean goToHuman()
    {
        System.out.println("Going to Position 4:" + rIO.getArmEncoder());
        return this.goToPos(4);
    }
*/
/*    public void humanControl()
    {
        if(false)//hIO.getArmOverrideBut())
        {
            this.Manual();
        }
        else
        {
            this.Auto();
        }
    }*/