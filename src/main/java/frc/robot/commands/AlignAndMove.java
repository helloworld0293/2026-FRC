package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.SwerveSubsystem;


public class AlignAndMove extends Command {
    private final SwerveSubsystem swerve;
    private boolean moveDone = false;
    private boolean alignDone = false;
    private boolean done = false;

    public AlignAndMove(SwerveSubsystem swerve){
        this.swerve = swerve;
        addRequirements(swerve);
    }

@Override
public void execute() {
    var fid = Robot.getHubTag();
    if (fid == null) return;
    Pose3d pose = fid.getTargetPose_RobotSpace();

    final double targetDistance = 10;
    final double distanceTolerance = 1;
    final double moveSpeed = 1;
    double distance = pose.getTranslation().getDistance(new Translation3d()); //for moving
    double x = pose.getX(); //gets apriltag's location relative to robot, for aligning



    // align
    double turnTolerance = 0.3;
    final double turnSpeed = 0.1;
    // in radians
    double turn;
    if (x > turnTolerance) {
        turn = -turnSpeed;
    } else if (x > -turnTolerance) {
        turn = turnSpeed;
    } else {
        turn = 0;
        alignDone = true;
    }

//move
    double move;
    if (distance < targetDistance - distanceTolerance) {
        move = -moveSpeed;
    } else if (distance > targetDistance + distanceTolerance) {
        move = moveSpeed;
    } else {
        move = 0;
        moveDone = true;
    }


    //when alignment and movement adjustments are complete
    if (moveDone && alignDone){
        done = true;
    }
    else {
        swerve.driveFieldOriented(new ChassisSpeeds(move,0,turn));
    }

}

@Override
    public void end(boolean interrupted) {
        swerve.driveFieldOriented(new ChassisSpeeds());
    }

@Override
    public boolean isFinished() {
   return done;
    }

}
