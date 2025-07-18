package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }
    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

    public Account addNewAccount(Account account) {
        if(account == null || account.getUsername() == null || account.getUsername().isBlank()) {
            return null;
        }
        if((account.getPassword() == null) || account.getPassword().length() < 4 ) {
            System.out.println("Password need to be longer than 4 characters.");
            return null;
        }
        Account existingAccount = accountDAO.getAccountByUserName(account.getUsername());
        if(existingAccount != null) {
            System.out.println("Username is already exist.");
            return null;
        }
        return accountDAO.insertAccount(account);
    }

    public Account verifyLogin(Account loginAttempt) {
        if(loginAttempt == null || loginAttempt.getUsername() == null || loginAttempt.getPassword() == null) {
            return null;
        }
        //get the exists account from database
        Account storedAccount = accountDAO.getAccountByUserName(loginAttempt.getUsername());
        if(storedAccount != null && storedAccount.getPassword().equals(loginAttempt.getPassword())) {
            return storedAccount;
        }
        return null;
    }
}

