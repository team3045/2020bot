/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Controller;
import edu.wpi.first.wpilibj.Joystick;

import javax.swing.ButtonGroup;

import com.ctre.phoenix.ButtonMonitor;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

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

  private final Joystick leftJoystick = new Joystick(0);
  private final Joystick rightJoystick = new Joystick(1);

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

  private int callNumber = 0;

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
    tankDrive();
    intake();
    printRPMs();
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

    leftTankMotor1Controller.set(leftPower * percentOutput);
    rightTankMotor1Controller.set(rightPower * percentOutput);
    leftTankMotor2Controller.set(leftPower * percentOutput);
    rightTankMotor2Controller.set(rightPower * percentOutput);

  }

  public void intake() {
    if (leftJoystick.getRawButton(1) || rightJoystick.getRawButton(1)) {
      intakeAxelController.set(0.875);
    } else {
      intakeAxelController.set(0.0);
    }
  }

  public void printRPMs() {
    double rightRPM = rightTankMotor1Controller.getSelectedSensorVelocity();
    if ((callNumber++ % 10) == 0) {
      System.err.println("Right side = " + rightRPM + " RPM");
    }
  }
}
