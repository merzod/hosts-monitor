package com.merzod.monitor.host;

/**
 * @author opavlenko
 */
public class Result {

    private State state;
    private String comments;
    private Exception exception;
    private Target target;

    public Result(Target t, String comments) {
        this.target = t;
        this.state = State.SUCCESS;
        this.comments = comments;
    }

    public Result(Target t, Exception e) {
        this.target = t;
        this.state = State.FAILED;
        this.exception = e;
    }

    public State getState() {
        return state;
    }

    public String getComments() {
        return comments;
    }

    public Exception getException() {
        return exception;
    }

    public Target getTarget() {
        return target;
    }

    @Override
    public String toString() {
        if(state == State.SUCCESS) {
            return state + ": " + comments;
        } else {
            return state + ": " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
        }
    }

    public static enum State {
        SUCCESS,
        FAILED
    }
}
