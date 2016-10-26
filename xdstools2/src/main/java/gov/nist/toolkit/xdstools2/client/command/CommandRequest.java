package gov.nist.toolkit.xdstools2.client.command;

/**
 *
 */
public interface CommandRequest<R, T> {
    /**
     * This is the method that must run the server call.
     * @param var1 parameter of the server call (context).
     */
    void run(R var1);
    void onSuccess(T var1);
}
