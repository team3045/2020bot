/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.networktables.NetworkTableInstance;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private final int horizontal = 0;
  private final int vertical = 1;
  private int count = 0;

  private final Joystick leftJoystick = new Joystick(0);
  private final Joystick rightJoystick = new Joystick(1);
  private final Joystick buttonBoard = new Joystick(2);

  /*@Override
  public boolean get() {
    // TODO Auto-generated method stub
    return false;
  }

  ;{
    
  @Override 
  public Void enable() {
      // TODO Auto-generated method stub
      
    }

  @Override
  public Void disable() {
    // TODO Auto-generated method stub

  }}; */

  private final WPI_TalonSRX rightTankMotor1Controller = new WPI_TalonSRX(1);
  private final WPI_TalonSRX rightTankMotor2Controller = new WPI_TalonSRX(2);
  private final WPI_TalonSRX leftTankMotor1Controller = new WPI_TalonSRX(3);
  private final WPI_TalonSRX leftTankMotor2Controller = new WPI_TalonSRX(4);
  private final WPI_TalonSRX intakeAxelController = new WPI_TalonSRX(5);
  private final WPI_TalonSRX magazineMiddleController = new WPI_TalonSRX(6);
  private final WPI_TalonSRX shooterLeftSide = new WPI_TalonSRX(7);
  private final WPI_TalonSRX shooterRightSide = new WPI_TalonSRX(8);
  private final WPI_TalonSRX winchController = new WPI_TalonSRX(9);

  private Solenoid leftIntakeSolenoid = new Solenoid(0);
  private Solenoid rightIntakeSolenoid = new Solenoid(1);

  private enum AutonomousState {
    START, MOVE_FORWARD, CHANGE, SHOOT, MAG
  }
  private AutonomousState autonState = AutonomousState.START;

  private Timer autoTimer = new Timer();
  private Timer turnTimer = new Timer();

  private int red1 = 1;
  private int red2 = 2;
  private int red3 = 3;
  private int red4 = 4;
  private int green3 = 5;
  private int blue3 = 6;
  private int blue2 = 7;
  private int green2 = 8;
  
  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    leftTankMotor1Controller.configVoltageCompSaturation(12.0, 100);
    leftTankMotor1Controller.enableVoltageCompensation(true);
    leftTankMotor2Controller.configVoltageCompSaturation(12.0, 100);
    leftTankMotor2Controller.enableVoltageCompensation(true);
    rightTankMotor1Controller.configVoltageCompSaturation(12.0, 100);
    rightTankMotor1Controller.enableVoltageCompensation(true);
    rightTankMotor2Controller.configVoltageCompSaturation(12.0, 100);
    rightTankMotor2Controller.enableVoltageCompensation(true);

    shooterLeftSide.configVoltageCompSaturation(12.0, 100);
    shooterLeftSide.enableVoltageCompensation(true);
    shooterRightSide.configVoltageCompSaturation(12.0, 100);
    shooterRightSide.enableVoltageCompensation(true);

    rightTankMotor1Controller.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    leftTankMotor2Controller.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);

    rightTankMotor1Controller.setSelectedSensorPosition(0);
    leftTankMotor2Controller.setSelectedSensorPosition(0);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    String piStatus = ntinst.getTable("Vision").getEntry("Pi Status").getString("bad");
    Number piGreen = ntinst.getTable("Vision").getEntry("numContours").getNumber(0);
    Number tHeight = ntinst.getTable("Vision").getEntry("target height").getNumber(0);
    Number tWidth = ntinst.getTable("Vision").getEntry("target width").getNumber(0);

    SmartDashboard.putString("Pi Status", piStatus);
    SmartDashboard.putNumber("Num Green", (double) piGreen);
    SmartDashboard.putNumber("target height", (double) tHeight);
    SmartDashboard.putNumber("target width", (double) tWidth);
    printRPMs();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {

    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    switch (m_autoSelected) {
    case kCustomAuto:
      autoTimer.stop();
      autoTimer.reset();
      autonState = AutonomousState.START;  
      rightTankMotor1Controller.setSelectedSensorPosition(0);
      leftTankMotor2Controller.setSelectedSensorPosition(0);
      break;
    case kDefaultAuto:
    turnTimer.reset();
    turnTimer.start();
    rightTankMotor1Controller.setSelectedSensorPosition(0);
    leftTankMotor2Controller.setSelectedSensorPosition(0);
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
    if(autonState == AutonomousState.START){
      autoTimer.reset();
      autoTimer.start();
      autonState = AutonomousState.MOVE_FORWARD;
    }else if(autonState == AutonomousState.MOVE_FORWARD){
      if (autoTimer.get() < 0.1) {
        break; // intentional don't do anything
      } else if(waitMove()){
        leftTankMotor2Controller.set(0.2); //proportional control? error*constant
        rightTankMotor1Controller.set(-0.2);
        leftTankMotor1Controller.set(ControlMode.Follower, 4);
        rightTankMotor2Controller.set(ControlMode.Follower,1);
      }else{
        leftTankMotor2Controller.set(0);
        rightTankMotor1Controller.set(0);
        leftTankMotor1Controller.set(ControlMode.Follower, 4);
        rightTankMotor2Controller.set(ControlMode.Follower,1);
        autoTimer.reset();
        autoTimer.start();
        autonState = AutonomousState.SHOOT;
      }
    }else if(autonState == AutonomousState.SHOOT){
      shooterLeftSide.set(0.5);
      shooterRightSide.set(-0.5);
      if(autoTimer.get() >= 1.5){
        autonState = AutonomousState.MAG;
        autoTimer.stop();
      }
    }else if(autonState == AutonomousState.MAG)
    {
      magazineMiddleController.set(0.47);
    }
    
      break;
    case kDefaultAuto:
    if(turnTimer.get()<=2.1){
      rightTankMotor1Controller.set(0.12);
      rightTankMotor2Controller.set(0.12);
      leftTankMotor1Controller.set(0.12);
      leftTankMotor2Controller.set(0.12);
    }else{
      rightTankMotor1Controller.set(0);
      rightTankMotor2Controller.set(0);
      leftTankMotor1Controller.set(0.0);
      leftTankMotor2Controller.set(0.0);
    }
    
    default:
      // Put default auto code here
      break;
    }


    
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    tankDrive();
    intake();
    shoot();
    reset();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
  public boolean waitMove(){
    System.err.println("aaaaaaaaaaaa: " + rightTankMotor1Controller.getSelectedSensorPosition());
    return Math.abs(rightTankMotor1Controller.getSelectedSensorPosition()) < 1300;
  }

  public void tankDrive() {
    final double percentOutput = 0.4;

    final double leftPower = leftJoystick.getRawAxis(vertical);
    final double rightPower = rightJoystick.getRawAxis(vertical);

    leftTankMotor1Controller.set(leftPower * percentOutput * -1);
    rightTankMotor1Controller.set(rightPower * percentOutput);
    leftTankMotor2Controller.set(ControlMode.Follower, 3);
    rightTankMotor2Controller.set(ControlMode.Follower, 1);

  }

  public void intake() {
    if (buttonBoard.getRawButton(red3)) {
      intakeAxelController.set(0.5);
      magazineMiddleController.set(0.7);
    } else if(buttonBoard.getRawButton(red4)){
      intakeAxelController.set(-0.5);
      magazineMiddleController.set(-0.7);
    }else{
      intakeAxelController.set(0.0);
      magazineMiddleController.set(0.0);
    }
  }

  public void printRPMs() {
    if (count++ % 20 == 0) {
      System.err.println("Right side = " + rightTankMotor1Controller.getSelectedSensorVelocity() + " RPM");
      System.err.println("Right side position = " + rightTankMotor1Controller.getSelectedSensorPosition());
      System.err.println("Right side talon %age: " + rightTankMotor1Controller.get());
      System.err.println("Right side talon current: " + rightTankMotor1Controller.getSupplyCurrent() + "A");
      System.err.println("Left side = " + leftTankMotor2Controller.getSelectedSensorVelocity() + " RPM");
      System.err.println("Left side position = " + leftTankMotor2Controller.getSelectedSensorPosition());
      System.err.println("Left side talon %age: " + leftTankMotor2Controller.get());
      System.err.println("Left side talon current: " + leftTankMotor2Controller.getSupplyCurrent() + "A");
      count = 1;
    }
  }

  public void shoot()
  {
    if(buttonBoard.getRawButton(blue3))
    {
      shooterLeftSide.set(0.5);
      shooterRightSide.set(-0.5);
    }else if(buttonBoard.getRawButton(blue2)){
      shooterLeftSide.set(-0.8);
      shooterRightSide.set(0.8);
    }else{
      shooterLeftSide.set(0.0);
      shooterRightSide.set(0.0);
    }
  }
  public void intakeArms()
  {
    leftIntakeSolenoid.set(buttonBoard.getRawButton(red1));
    rightIntakeSolenoid.set(buttonBoard.getRawButton(red1));
  }
  public void magOut()
  {
    if(buttonBoard.getRawButton(red2))
      magazineMiddleController.set(-0.5);
  }
  //The following methods are not used in game
  public void reset()
  {
    if(leftJoystick.getRawButton(1))
    {
      rightTankMotor1Controller.setSelectedSensorPosition(0);
      leftTankMotor2Controller.setSelectedSensorPosition(0);
    }
  }
}
