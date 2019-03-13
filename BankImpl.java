//-------------------------------------------------------------------------------------------------
// BankImpl.java
//
// implementation of the Bank
//
import java.io.*;
import java.util.*;

public class BankImpl implements Bank {
    private int n;            // the number of threads in the system
    private int m;            // the number of resources

    private int[] available;    // the amount available of each resource
    private int[][] maximum;    // the maximum demand of each thread
    private int[][] allocation;    // the amount currently allocated to each thread
    private int[][] need;        // the remaining needs of each thread
    private boolean[] isFulfilled;

    boolean state = false;

    private void showAllMatrices(int[][] alloc, int[][] max, int[][] need, String msg) {
        System.out.println();
        System.out.print("Available: [");
        for (int i = 0; i < m-1; i++)
            System.out.print(available[i]+" ");
        System.out.println(available[m-1]+"]");
        System.out.println();

        System.out.println("\tAllocation\t\t\t Maximum \t\t\t Need");
        for (int i = 0; i < n; i++) {
            boolean isZero = false;
            if(need[i][0] == 0 && need [i][1] == 0 && need[i][2] == 0)
                isZero = true;


            System.out.print(i + ": [ ");
            for (int j = 0; j < m; j++) {
                if(!isZero)    {
                    System.out.print(alloc[i][j] + " ");
                }
                else
                {
                    System.out.print("- ");
                }
            }
            System.out.print("]\t\t\t");

            System.out.print("[ ");
            for (int j = 0; j < m; j++) {
                if(!isZero)    {
                    System.out.print(max[i][j] + " ");
                }
                else
                {
                    System.out.print("- ");
                }
            }
            System.out.print("]\t\t\t");
            System.out.print("[ ");
            for (int j = 0; j < m; j++) {
                if(!isZero)    {
                    System.out.print(need[i][j] + " ");
                }
                else
                {
                    System.out.print("- ");
                }
            }
            System.out.print("]\t\t\t");
            System.out.println();
        }
    }

    public void showAll()    {
        showAllMatrices(allocation, maximum, need, "");
    }

    public BankImpl(int[] resources) {      // create a new bank (with resources)
        m = resources.length;
        n =  Customer.COUNT;

        available = new int[m];
        System.arraycopy(resources,0,available,0,m);

        isFulfilled = new boolean[Customer.COUNT];
        for(int i = 0; i < isFulfilled.length; i++) {
            isFulfilled[i] = false;
        }
        maximum = new int[Customer.COUNT][];
        allocation = new int[Customer.COUNT][];
        need = new int[Customer.COUNT][];    }

    public void addCustomer(int threadNumber, int[] allocated, int[] maxDemand) {
        maximum[threadNumber] = new int[m];
        allocation[threadNumber] = new int[m];
        need[threadNumber] = new int[m];

        System.arraycopy(allocated, 0, allocation[threadNumber], 0 , allocated.length);
        System.arraycopy(maxDemand, 0, maximum[threadNumber], 0, maxDemand.length);

        int[] tempNeed = new int[maxDemand.length];
        for(int i = 0; i < maxDemand.length; i++)
        {
            tempNeed[i] = maxDemand[i] - allocated[i];
        }
        System.arraycopy(tempNeed, 0, need[threadNumber], 0, tempNeed.length);
    }

    public void getState() {        // output state for each thread
        showAllMatrices(allocation, maximum, need, "Bleh");
        for (int i = 0;i<n;i++)
        {
            for (int j=0;j<1;j++)
            {
                if (need[i][j]<=available[j]&& need[i][j+1]<=available[j+1]&&need[i][j+2]<=available[j+2])
                {
                    state= true;
                }
            }

        }
        if (state)
        {
            System.out.println("No deadlock!");
        }
        if (!state)
        {
            System.out.println("DEADLOCK!");
        }
        System.out.println();
    }

    private boolean isSafeState(int threadNumber, int[] request) {
        System.out.print(" [");
        for (int i = 0; i < m; i++)
            System.out.print(request[i] + " ");
        System.out.println("]");


        for (int i = 0; i < m; i++)
            if (request[i] > available[i]) {
                return false;
            }

        boolean[] isFinishable = new boolean[n];
        for (int i = 0; i < n; i++)
            isFinishable[i] = false;

        int[] tempAvailable = new int[m];
        System.arraycopy(available, 0, tempAvailable, 0, available.length);

        for (int i = 0; i < m; i++) {
            tempAvailable[i] -= request[i];
            need[threadNumber][i] -= request[i];
            allocation[threadNumber][i] += request[i];
        }

        boolean failed = false;
        for(int i = 0 ; i < m; i++) {
            if(tempAvailable[i] < 0 || allocation[threadNumber][i] < 0) {
                for (int j = 0; j < m; j++) {
                    need[threadNumber][j] += request[j];
                    allocation[threadNumber][j] -= request[j];
                }
                return false;
            }
        }



        for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (!isFinishable[j]) {
                        boolean temp = true;
                        for (int k = 0; k < m; k++) {
                            if (need[j][k] > tempAvailable[k] && tempAvailable[k] > 0)
                                temp = false;
                        }
                        if (temp) {
                            isFinishable[j] = true;
                            for (int x = 0; x < m; x++)
                                tempAvailable[x] += allocation[j][x];
                        }
                    }
                }
            }


            for (int i = 0; i < m; i++) {
                need[threadNumber][i] += request[i];
                allocation[threadNumber][i] -= request[i];
            }

            boolean value = true;
            for (int i = 0; i < n; i++)
                if (!isFinishable[i]) {
                     value = false;
                    break;
                }

            return value;
        }


    // make request for resources. will block until request is satisfied safely
    public synchronized boolean requestResources(int threadNumber, int[] request) {
        System.out.print("\n Attempting to Add resources to #" + threadNumber);
            if (!isSafeState(threadNumber,request)) {
                System.out.println("Failed to add resources");
                return false;
            }

            for (int i = 0; i < m; i++) {
                available[i] -= request[i];
                allocation[threadNumber][i] += request[i];
                need[threadNumber][i] = maximum[threadNumber][i] - allocation[threadNumber][i];
            }
            System.out.println("Succesfully added resources!");


            boolean isNeedEmpty = false;
            if(need[threadNumber][0] == 0 &&
                need[threadNumber][1] == 0 &&
                need[threadNumber][2] == 0) {
                System.out.println("Thread #" + threadNumber + " is full. Releasing memory!");
                int[] temp = new int[allocation[threadNumber].length];
                for(int i = 0; i < allocation[threadNumber].length; i++)    {
                    temp[i] = allocation[threadNumber][i];
                }
                releaseResources(threadNumber, temp);
            }
            else
            {
                showAllMatrices(allocation, maximum, need, "");
            }



            return true;
        }

    public synchronized void releaseResources(int threadNumber, int[] release) {
            System.out.print("\n Attempting to Release Resources from #" + threadNumber);

            int[] tempArray = new int[release.length];
            for(int i = 0; i < release.length; i++)
            {
                tempArray[i] = -1 * release[i];
            }

            if(!isSafeState(threadNumber, tempArray)) {
                System.out.println("Failed to release");
                return;
            }

            System.out.println("Release successful!");

            boolean isNeedZero = false;
            if(need[threadNumber][0] == 0 && need[threadNumber][1] == 0 && need[threadNumber][2] == 0)  {
                isNeedZero = true;
                isFulfilled[threadNumber] = true;
            }

            if(!isNeedZero)  {
                for (int i = 0; i < m; i++) {
                    available[i] += release[i];
                    allocation[threadNumber][i] -= release[i];
                    need[threadNumber][i] = maximum[threadNumber][i] + allocation[threadNumber][i];
                }
            }
            else
            {
                for (int i = 0; i < m; i++) {
                    available[i] += release[i];
                }

            }

            showAllMatrices(allocation, maximum, need, "");
    }

    public boolean isFulfilled(int threadNumber)    {
        return isFulfilled[threadNumber];
    }
}

