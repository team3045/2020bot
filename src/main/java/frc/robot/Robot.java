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
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

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

  private WPI_TalonSRX rightTankMotor1Controller = new WPI_TalonSRX(1);
  private WPI_TalonSRX rightTankMotor2Controller = new WPI_TalonSRX(2);
  private WPI_TalonSRX leftTankMotor1Controller = new WPI_TalonSRX(3);
  private WPI_TalonSRX leftTankMotor2Controller = new WPI_TalonSRX(4);
  private WPI_TalonSRX intakeAxelController = new WPI_TalonSRX(5);
  private WPI_TalonSRX magazineMiddleController = new WPI_TalonSRX(6);
  private WPI_TalonSRX shooterLeftSide = new WPI_TalonSRX(7);
  private WPI_TalonSRX shooterRightSide = new WPI_TalonSRX(8);
  private WPI_TalonSRX winchController = new WPI_TalonSRX(9);

  private double practiceEncoderPos = 0;
  private enum PracticeEncodeState {
    DISABLE,
    MOVE,
    CHANGE,
    WAIT,
  }
  private PracticeEncodeState encodeState = PracticeEncodeState.DISABLE;
  private Timer moveTimer = new Timer();

  private int red1 = 1;
  private int red2 = 2;
  private int red3 = 3;
  private int red4 = 4;
  private int green3 = 5;
  private int blue3 = 6;
  private int blue2 = 7;
  private int green2 = 8;
  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    rightTankMotor1Controller.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0); 
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
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
    practiceSetMove();
    if(!leftJoystick.getRawButton(2))
      tankDrive();
    intake();
    printRPMs();
    shoot();
    magazineMove();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void tankDrive() {
    final double percentOutput = .5;

    double leftPower = leftJoystick.getRawAxis(vertical);
    double rightPower = rightJoystick.getRawAxis(vertical);

    leftTankMotor1Controller.set(leftPower * percentOutput * -1);
    rightTankMotor1Controller.set(rightPower * percentOutput);
    leftTankMotor2Controller.set(ControlMode.Follower, 3);
    rightTankMotor2Controller.set(ControlMode.Follower,1);

  }

  public void intake() {
    if (buttonBoard.getRawButton(red1)) {
      intakeAxelController.set(0.875);
    } else if(buttonBoard.getRawButton(red2)){
      intakeAxelController.set(-0.875);
    }else{
      intakeAxelController.set(0.0);
    }
  }

  public void printRPMs() {
    double rightRPM = rightTankMotor1Controller.getSelectedSensorVelocity();
    if (count++ % 50 == 0) {
      System.err.println("Right side = " + rightRPM + " RPM");
      System.err.println(encodeState);
      count = 1;
    }
  }
  public void practiceSetMove()
  {
    if(leftJoystick.getRawButton(2)){
      if(encodeState == PracticeEncodeState.DISABLE){
        practiceEncoderPos = 0;
        encodeState = PracticeEncodeState.MOVE;
      }else if(encodeState == PracticeEncodeState.MOVE){
        if(practiceEncoderPos <= 500){
          leftTankMotor1Controller.set(0.4);
          rightTankMotor1Controller.set(-0.4);
          leftTankMotor2Controller.set(ControlMode.Follower, 3);
          rightTankMotor2Controller.set(ControlMode.Follower,1);

          rightTankMotor1Controller.setSelectedSensorPosition(10);
          
          System.err.println("Practice encoder position: " + practiceEncoderPos);
          
        }else{
          encodeState = PracticeEncodeState.CHANGE;
        }
      }else if(encodeState == PracticeEncodeState.CHANGE){
        leftTankMotor1Controller.set(0.0);
        rightTankMotor1Controller.set(0.0);
        leftTankMotor2Controller.set(ControlMode.Follower, 3);
        rightTankMotor2Controller.set(ControlMode.Follower,1);
        practiceEncoderPos = 0;
        moveTimer.reset();
        moveTimer.start();
        encodeState = PracticeEncodeState.WAIT;
      }else if(encodeState == PracticeEncodeState.WAIT)
      {
        if(moveTimer.get() >= 3.0){
          encodeState = PracticeEncodeState.DISABLE;
        }
      }
    }else{
      encodeState = PracticeEncodeState.DISABLE;
    }
  }

  public void shoot()
  {
    if(buttonBoard.getRawButton(blue3))
    {
      shooterLeftSide.set(0.9);
      shooterRightSide.set(-0.9);
    }else if(buttonBoard.getRawButton(blue2)){
      shooterLeftSide.set(-0.9);
      shooterRightSide.set(0.9);
    }else{
      shooterLeftSide.set(0.0);
      shooterRightSide.set(0.0);
    }
  }
  public void magazineMove()
  {
    if(buttonBoard.getRawButton(red4)){
      magazineMiddleController.set(0.8);
    }else if(buttonBoard.getRawButton(red3)){
      magazineMiddleController.set(-0.8);
    }else{
      magazineMiddleController.set(0.0);
    }
  }
}
