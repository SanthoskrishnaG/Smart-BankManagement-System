package backend;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

public class Bank implements Serializable {
    private Map<String, Account> accounts;
    private int nextAccountNumber;
    private static final String DATA_FILE = "backend/backend_data.dat"; // Pointing to the new backend folder

    public Bank() {
        accounts = new HashMap<>();
        nextAccountNumber = 1001;
        loadData();
    }

    public String createAccount(Customer customer, double initialDeposit) {
        String accountNumber = "ACCT" + nextAccountNumber++;
        Account newAccount = new Account(accountNumber, customer, initialDeposit);
        accounts.put(accountNumber, newAccount);
        saveData();
        return accountNumber;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber.toUpperCase());
    }
    
    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber.toUpperCase());
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public Account authenticateUser(String name, String pin) {
        for (Account acc : accounts.values()) {
            if (acc.getCustomer().getName().equalsIgnoreCase(name) && acc.getCustomer().getPin().equals(pin)) {
                return acc;
            }
        }
        return null;
    }

    public void saveData() {
        File dir = new File("backend");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
            oos.writeInt(nextAccountNumber);
        } catch (IOException e) {
            System.err.println("Error saving bank data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                accounts = (Map<String, Account>) ois.readObject();
                nextAccountNumber = ois.readInt();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading bank data: " + e.getMessage());
            }
        }
    }
}
