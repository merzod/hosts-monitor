package com.merzod.monitor.host;

/**
 * @author opavlenko
 */
public class Result {

    private State state;
    private String comments;
    private Exception exception;

    public Result(String comments) {
        this.state = State.SUCCESS;
        this.comments = comments;
    }

    public Result(Exception e) {
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
