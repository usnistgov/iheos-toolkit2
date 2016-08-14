package gov.nist.toolkit.xdstools2.client.command;

/**
 *
 */
public interface CommandRequest<R, T> {
    void run(R var1);
    void onSuccess(T var1);
}
