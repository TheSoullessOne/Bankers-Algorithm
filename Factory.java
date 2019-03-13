//-------------------------------------------------------------------------------------------------
// Factory.java
//
// Factory class that creates the bank and each bank customer
// Usage:  java Factory 10 5 7
import java.io.*;
import java.util.*;

public class Factory {
    public static void main(String[] args) throws IOException {
        String filename = "infile.txt";
        int nResources = args.length;
        int[] available = new int[nResources];
        for (int i = 0; i < nResources; i++) {
            available[i] = Integer.parseInt(args[i].trim());
        }

        Bank theBank = new BankImpl(available);
        int[] maxDemand = new int[nResources];
        int[] maxAllocated = new int[nResources];
        Thread[] workers = new Thread[Customer.COUNT];      // the customers


        int[][] alloMatrix = new int[Customer.COUNT][nResources];
        int[][] demandMatrix = new int[Customer.COUNT][nResources];

        BufferedReader br = new BufferedReader(new FileReader(filename));

        String st;

        // read in values and initialize the matrices
        // to do
        // ...

        int threadNum = 0;
        for(int i = 0; i < Customer.COUNT; i++) {
            st = br.readLine();
            String[] parser = st.split(",");

            maxAllocated[0] = Integer.valueOf(parser[0]);
            maxAllocated[1] = Integer.valueOf(parser[1]);
            maxAllocated[2] = Integer.valueOf(parser[2]);
            maxDemand[0] = Integer.valueOf(parser[3]);
            maxDemand[1] = Integer.valueOf(parser[4]);
            maxDemand[2] = Integer.valueOf(parser[5]);


            threadNum = i;

            System.out.println("Adding customer " + threadNum + "...");

            workers[threadNum] = new Thread(new Customer(threadNum, maxDemand, theBank));
            theBank.addCustomer(threadNum, maxAllocated, maxDemand);
        }

//        ++threadNum;        //theBank.getCustomer(threadNum);
        //resourceNum = 0;

        Scanner input = new Scanner(System.in);

        System.out.println("<Rq | Rl> <Process number> <R0 resources> <R1 resources> <R2 resrouces>\n" +
                            "\"*\" to see all matrices; \"exit\" to quit program");
        String strInput = input.nextLine();
        String[] parsedInput = strInput.split(" ");

        while(!strInput.equalsIgnoreCase("exit")) {
            if (!strInput.equals("*")) {
                int[] resourceArray = new int[nResources];
                resourceArray[0] = Integer.valueOf(parsedInput[2]);
                resourceArray[1] = Integer.valueOf(parsedInput[3]);
                resourceArray[2] = Integer.valueOf(parsedInput[4]);

                boolean correct = true;
                for(int i = 0; i < resourceArray.length; i++)   {
                    if(resourceArray[i] < 0)
                        correct  =false;
                }

                if (!((BankImpl) theBank).isFulfilled(Integer.valueOf(parsedInput[1])) &&
                        correct &&
                        parsedInput.length < 6 &&
                        Integer.valueOf(parsedInput[1]) < Customer.COUNT) {
                    if (parsedInput[0].equalsIgnoreCase("Rq")) {
                        theBank.requestResources(Integer.valueOf(parsedInput[1]), resourceArray);
                    } else if (parsedInput[0].equalsIgnoreCase("Rl")) {
                        theBank.releaseResources(Integer.valueOf(parsedInput[1]), resourceArray);
                    } else {
                        System.out.println("Not a valid input for menu!\n\n");
                    }
                }
                else if(((BankImpl) theBank).isFulfilled(Integer.valueOf(parsedInput[1])))  {
                    System.out.println("That process is already complete.\n\n");
                } else  {
                    System.out.println("Incorrect parameters.\n\n");
                }
            }
            else
            {
                theBank.showAll();
            }
            System.out.println("<Rq | Rl> <Process number> <R0 resources> <R1 resources> <R2 resrouces>\n" +
                    "\"*\" to see all matrices; \"exit\" to quit program");
            strInput = input.nextLine();
            parsedInput = strInput.split(" ");

        }
    }
}