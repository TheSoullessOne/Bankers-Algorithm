//-------------------------------------------------------------------------------------------------
// Customer.java
//
public class Customer implements Runnable {
    public static final int COUNT = 5;    // number of threads

    private int numberOfResources;     // N different resources
    private int[] maxDemand;        // maximum this thread will demand
    private int customerNumber;        // customer number
    private int[] request;          // request it is making

    private java.util.Random randomNumber;  // random number generator

    private Bank theBank;           // synchronizing object

    public Customer(int customerNumber, int[] maxDemand, Bank theBank) {
        this.customerNumber = customerNumber;
        this.maxDemand = new int[maxDemand.length];
        this.theBank = theBank;

        System.arraycopy(maxDemand,0,this.maxDemand,0,maxDemand.length);
        numberOfResources = maxDemand.length;
        request = new int[numberOfResources];
        randomNumber = new java.util.Random();
    }

    public void run() {
        boolean isRunnable = true;
        while (isRunnable) {
            try {
                SleepUtilities.nap();       // take a nap
                for (int i = 0; i < numberOfResources; i++) {
                    request[i] = randomNumber.nextInt(maxDemand[i]+1);
                }

                if (theBank.requestResources(customerNumber, request)) {
                    SleepUtilities.nap();   // use and release the resources
                    theBank.releaseResources(customerNumber, request);
                }
            } catch (InterruptedException ie) { isRunnable = false; }
        }
        System.out.println("Thread # " + customerNumber + " I'm interrupted.");
    }
}

