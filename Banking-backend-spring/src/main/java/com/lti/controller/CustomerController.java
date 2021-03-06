package com.lti.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lti.model.AccountSummary;
import com.lti.model.AdminGetRegisterStatus;
import com.lti.model.AdminTransactionView;
import com.lti.model.CredentialStatus;
import com.lti.model.Login;
import com.lti.model.LoginStatus;
import com.lti.model.NewBeneficiaryStatus;
import com.lti.model.NewPasswordStatus;
import com.lti.model.Picture;
import com.lti.model.RegisterStatus;
import com.lti.model.Status;
import com.lti.model.TransactionStatus;
import com.lti.model.Transactions;
import com.lti.repository.CustomerRepository;
import com.lti.entity.Account;
import com.lti.entity.AccountCredential;
import com.lti.entity.AccountDetail;
import com.lti.entity.Payee;
import com.lti.entity.Registration;
import com.lti.entity.Transaction;
import com.lti.service.CustomerService;
import com.lti.service.OtpService;
import com.lti.service.ServiceException;


@RestController
@CrossOrigin
public class CustomerController {

	@Autowired
	private CustomerRepository custRepo;

	@Autowired
	private CustomerService customerService;

	@PostMapping("/register")
	public RegisterStatus register(@RequestBody Registration customer) {

		try {

			long id = customerService.register(customer);
			RegisterStatus status = new RegisterStatus();
			status.setStatus(true);
			status.setMessage("Registration successfull!!!");
			status.setReferenceId(id);
			return status;
		}
		catch(ServiceException e) {
			RegisterStatus status = new RegisterStatus();
			status.setStatus(false);
			status.setMessage(e.getMessage());
			return status;
		}
	}

	@PostMapping("/userlogin")
	public LoginStatus login(@RequestBody Login login) {
		try {
			Account account = customerService.login(login.getCustomerId(), login.getLoginPassword());
			LoginStatus loginStatus = new LoginStatus();
			loginStatus.setStatus(true);
			loginStatus.setMessage("Login successful!");
			loginStatus.setCustomerId(account.getCustomerId());
			loginStatus.setAccounts(customerService.getAccounts(account.getCustomerId()));
			Registration registration = new  Registration();
			loginStatus.setName(registration.getFirstName());
			loginStatus.setName(registration.getMiddleName());
			loginStatus.setName(registration.getLastName());


			return loginStatus;
		}
		catch(ServiceException e) {
			LoginStatus loginStatus = new LoginStatus();
			loginStatus.setStatus(false);
			loginStatus.setMessage(e.getMessage());		
			return loginStatus;
		}
	}

	

	@GetMapping("/sendOtp")
	public String otp(@RequestParam("customerId") int id) throws MessagingException{
		Account acc = custRepo.findCustomerByCustomerId(id);
		String email = acc.getRegistration().getEmailId();
		return customerService.sendOtp(email);
	}


	@PostMapping("/impstransaction")
	public TransactionStatus imps(@RequestBody Transactions transaction) throws MessagingException {
		try {
			String referenceId = customerService.impsTransaction(transaction);

			TransactionStatus transactionStatus = new TransactionStatus();
			transactionStatus.setStatus(true);
			transactionStatus.setRefernceNo(referenceId);
			transactionStatus.setMessage("Amount has been debited from your account and will be credited to the receipent's account");

			return transactionStatus;
		}
		catch (ServiceException e) {
			TransactionStatus transactionStatus = new TransactionStatus();
			transactionStatus.setStatus(false);
			transactionStatus.setMessage(e.getMessage());
			return transactionStatus;
		}
	}

	@PostMapping("/nefttransaction")
	public TransactionStatus neft(@RequestBody Transactions transaction) {
		try {
			String referenceId = customerService.neftTransaction(transaction);

			TransactionStatus transactionStatus = new TransactionStatus();
			transactionStatus.setStatus(true);
			transactionStatus.setRefernceNo(referenceId);
			transactionStatus.setMessage("Amount has been debited from your account and will be credited to the receipent's account");

			return transactionStatus;
		}
		catch (ServiceException e) {
			TransactionStatus transactionStatus = new TransactionStatus();
			transactionStatus.setStatus(false);
			transactionStatus.setMessage(e.getMessage());
			return transactionStatus;
		}
	}

	@PostMapping("/rtgstransaction")
	public TransactionStatus rtgs(@RequestBody Transactions transaction) {
		try {
			String referenceId = customerService.rtgsTransaction(transaction);

			TransactionStatus transactionStatus = new TransactionStatus();
			transactionStatus.setStatus(true);
			transactionStatus.setRefernceNo(referenceId);
			transactionStatus.setMessage("Amount has been debited from your account and will be credited to the receipent's account");

			return transactionStatus;
		}
		catch (ServiceException e) {
			TransactionStatus transactionStatus = new TransactionStatus();
			transactionStatus.setStatus(false);
			transactionStatus.setMessage(e.getMessage());
			return transactionStatus;
		}
	}

	//below codes are for admin part...
	@GetMapping("/accountview")
	public List<AccountDetail> basicDetails(@RequestParam ("customerId") Long custId) {
		try {
			List<AccountDetail> detail = customerService.getDetailsOfPerticularCustomer(custId);
			Status status = new  Status();
			status.setStatus(true);
			status.setMessage("Retrieved Transactions!");
			return detail;
		}
		catch(ServiceException e) {
			Status status = new  Status();
			List<AccountDetail> detail =new ArrayList<AccountDetail>();
			status.setStatus(false);
			status.setMessage(e.getMessage());		
			return detail;


		}
	}

	@GetMapping("/adminaccountview")
	public List<AccountDetail> basicDetails() {
		try {
			List<AccountDetail> detail = customerService.getDetailsForAdmin();
			Status status = new  Status();
			status.setStatus(true);
			status.setMessage("Retrieved Transactions!");
			return detail;
		}
		catch(ServiceException e) {
			Status status = new  Status();
			List<AccountDetail> detail =new ArrayList<AccountDetail>();
			status.setStatus(false);
			status.setMessage(e.getMessage());		
			return detail;


		}
	}
	@GetMapping("/TransactionViewAdmin")
	public List<AdminTransactionView> transaction(@RequestParam("accNumber") Long acc) {
		try {
			List<Transaction> list =  customerService.transactionViewByAdmin(acc,acc);
			List<AdminTransactionView> viewList = new ArrayList<AdminTransactionView>();

			for(Transaction t :list) {
				System.out.println(t.getTransactionId()+" ,"+t.getFromAccount().getAccountNumber()+" -> "+t.getToAccount().getAccountNumber()+" , "+t.getAmount());
				AdminTransactionView view1 = new  AdminTransactionView();
				view1.setStatus(true);
				view1.setMessage("Retrieved Transactions!");
				view1.setTransactionId(t.getTransactionId());
				view1.setFromAccount(t.getFromAccount().getAccountNumber());
				view1.setToAccount(t.getToAccount().getAccountNumber());
				view1.setAmount(t.getAmount());
				view1.setMode(t.getModeOfTransaction().name());
				view1.setRemark(t.getRemarks());
				view1.setTransactionDate(t.getTransactionDate().toLocalDate());
				viewList.add(view1);
			}

			return viewList;
		}
		catch(ServiceException e) {
			AdminTransactionView view = new  AdminTransactionView();
			List<AdminTransactionView> viewList = new ArrayList<AdminTransactionView>();
			view.setStatus(false);
			view.setMessage(e.getMessage());		
			return viewList;


		}
	}

	@GetMapping("/TransactionViewUser")
	public List<AdminTransactionView> userTransaction(@RequestParam("custId") Long cust) {
		try {
			List<Transaction> list =  customerService.transactionViewByUser(cust);
			List<AdminTransactionView> viewList = new ArrayList<AdminTransactionView>();

			for(Transaction t :list) {
				System.out.println(t.getTransactionId()+" ,"+t.getFromAccount().getAccountNumber()+" -> "+t.getToAccount().getAccountNumber()+" , "+t.getAmount());
				AdminTransactionView view1 = new  AdminTransactionView();
				view1.setStatus(true);
				view1.setMessage("Retrieved Transactions!");
				view1.setTransactionId(t.getTransactionId());
				view1.setFromAccount(t.getFromAccount().getAccountNumber());
				view1.setToAccount(t.getToAccount().getAccountNumber());
				view1.setAmount(t.getAmount());
				view1.setMode(t.getModeOfTransaction().name());
				view1.setRemark(t.getRemarks());
				view1.setTransactionDate(t.getTransactionDate().toLocalDate());
				viewList.add(view1);
			}

			return viewList;
		}
		catch(ServiceException e) {
			AdminTransactionView view = new  AdminTransactionView();
			List<AdminTransactionView> viewList = new ArrayList<AdminTransactionView>();
			view.setStatus(false);
			view.setMessage(e.getMessage());		
			return viewList;


		}
	}

	@GetMapping("/RequestViewByAdmin")
	public List<AdminGetRegisterStatus> requestView() {
		try {
			List<Registration> list =  customerService.RegisterRequestAction();
			List<AdminGetRegisterStatus> viewList = new ArrayList<AdminGetRegisterStatus>();

			for(Registration t :list) {
				AdminGetRegisterStatus view1 = new  AdminGetRegisterStatus();
				view1.setStatus(true);
				view1.setMessage("Retrieved Account Request!");
				view1.setReferenceId(t.getReferenceNo());
				view1.setTitle(t.getTitle());
				view1.setFirstName(t.getFirstName());
				view1.setMiddleName(t.getMiddleName());
				view1.setLastName(t.getLastName());
				view1.setFatherName(t.getFatherName());
				view1.setMobileNo(t.getMobileNo());
				view1.setEmailId(t.getEmailId());
				view1.setAadhaarNo(t.getAadhaarNo());
				view1.setPanCard(t.getPanCard());
				view1.setDateOfBirth(t.getDateOfBirth());
				view1.setResidentialAddress(t.getResidentialAddress());
				view1.setOccupation(t.getOccupation());
				view1.setIncomeSource(t.getIncomeSource());
				view1.setAnnualIncome(t.getAnnualIncome());
				view1.setRevenueRegisterNo(t.getRevenueRegisterNo());
				view1.setGstNumber(t.getGstNumber());
				viewList.add(view1);
			}

			return viewList;
		}
		catch(ServiceException e) {
			AdminGetRegisterStatus status = new AdminGetRegisterStatus();
			List<AdminGetRegisterStatus> viewList = new ArrayList<AdminGetRegisterStatus>();
			status.setStatus(false);
			status.setMessage(e.getMessage());	
			viewList.add(status);
			return viewList;
		}
	}

	@PostMapping("/setcredential")
	public CredentialStatus setCredential(@RequestBody AccountCredential account ) {

		try {
			long id= customerService.updateCredential(account);
			CredentialStatus status = new CredentialStatus();
			status.setStatus(true);
			status.setMessage("updation successful !");
			status.setCustId(id);
			return status;
		}
		catch(ServiceException e) {
			CredentialStatus status = new CredentialStatus();
			status.setStatus(false);
			status.setMessage(e.getMessage());
			return status;
		}

	}

	@GetMapping("/FileViewByAdmin")
	public List<AdminGetRegisterStatus> requestFileView(@RequestParam Long ref) {
		try {
			List<AdminGetRegisterStatus> viewList = new ArrayList<AdminGetRegisterStatus>();
			Registration reg = (Registration) customerService.registerFileView(ref);
			AdminGetRegisterStatus view1 = new  AdminGetRegisterStatus();
			view1.setStatus(true);
			view1.setMessage("Retrieved Account Request!");
			view1.setAadhaarNo(reg.getAadhaarNo());
			view1.setRevenueRegisterNo(reg.getRevenueRegisterNo());
			view1.setGstNumber(reg.getGstNumber());
			view1.setPanCard(reg.getPanCard());
			view1.setAadharPic(reg.getAadharPic());
			view1.setPanPic(reg.getPanPic());
			view1.setGstProof(reg.getGstNumber());
			view1.setLightBill(reg.getLightBill());
			viewList.add(view1);

			return viewList;
		}
		catch(ServiceException e) {
			AdminGetRegisterStatus status = new AdminGetRegisterStatus();
			List<AdminGetRegisterStatus> viewList = new ArrayList<AdminGetRegisterStatus>();
			status.setStatus(false);
			status.setMessage(e.getMessage());	
			viewList.add(status);
			return viewList;
		}
	}


	@DeleteMapping("/remove")
	public Status rejectRequest(@RequestParam ("referenceId") Long ref) {
		//int res =  customerService.remove(ref);

		customerService.deleteRow(ref);
		Status status = new  Status();
		status.setStatus(true);
		status.setMessage("Request rejected successfully");

		return status;
	}

	@PostMapping("/pic-upload")
	public Status upload(Picture picDetails) {

		long referenceId = picDetails.getReferenceId();

		String imgUploadLocation = "e:/uploads/";

		String uploadedFileName1 = picDetails.getAadharPic().getOriginalFilename();
		String newFileName1 = referenceId + "-" + uploadedFileName1;
		String targetFileName1 = imgUploadLocation + newFileName1;

		String uploadedFileName2 = picDetails.getPanPic().getOriginalFilename();
		String newFileName2 = referenceId + "-" + uploadedFileName2;
		String targetFileName2 = imgUploadLocation + newFileName2;

		String uploadedFileName3 = picDetails.getLightBill().getOriginalFilename();
		String newFileName3 = referenceId + "-" + uploadedFileName3;
		String targetFileName3 = imgUploadLocation + newFileName3;


		try {
			FileCopyUtils.copy(picDetails.getAadharPic().getInputStream(), new FileOutputStream(targetFileName1));
			FileCopyUtils.copy(picDetails.getPanPic().getInputStream(), new FileOutputStream(targetFileName2));
			FileCopyUtils.copy(picDetails.getLightBill().getInputStream(), new FileOutputStream(targetFileName3));
		}
		catch(IOException e) {
			e.printStackTrace(); //hope no error would occur
			Status status = new Status();
			status.setStatus(false);
			status.setMessage("Picture upload failed!");
		}
		//System.out.println(newFileName1+newFileName2+newFileName3+newFileName4);
		customerService.updatePicture(referenceId, newFileName1, newFileName2, newFileName3);

		Status status = new Status();
		status.setStatus(true);
		status.setMessage("Profilepic uploaded successfully!");
		return status;
	}

	@GetMapping("/profile")
	//public Customer profile(@RequestParam("customerId")int id) {
	//we need to take of help of HttpServletRequest object in below code
	public Registration profile(@RequestParam("referenceId") long id, HttpServletRequest request) {

		//HttpSession session = request.getSession();
		//session.setAttribute("otp", 123);

		Registration registration = customerService.get(id);

		//the problem is the image is in some folder outside this project
		//because of this, on the client we will not be able to access the same
		//we need to write code to copy image from d:/uploads folder to a folder inside our project

		String projPath = request.getServletContext().getRealPath("/");
		System.out.println(projPath);

		String tempDownloadPath = projPath + "/downloads/";
		File f = new File(tempDownloadPath);
		if(!f.exists())
			f.mkdir();

		String targetFile1 = tempDownloadPath + registration.getAadharPic();
		String targetFile2 = tempDownloadPath + registration.getPanPic();
		String targetFile3 = tempDownloadPath + registration.getLightBill();

		//reading the original location where the image is present
		String uploadedImagesPath = "e:\\uploads\\";
		String sourceFile1 = uploadedImagesPath + registration.getAadharPic();
		String sourceFile2 = uploadedImagesPath + registration.getPanPic();
		String sourceFile3 = uploadedImagesPath + registration.getLightBill();

		try {
			FileCopyUtils.copy(new File(sourceFile1), new File(targetFile1));
			FileCopyUtils.copy(new File(sourceFile2), new File(targetFile2));
			FileCopyUtils.copy(new File(sourceFile3), new File(targetFile3));
		}
		catch (IOException e) {
			e.printStackTrace(); //hoping for no error will occur
		}

		return registration;
	}

	@PostMapping("/SetPassword")
	public NewPasswordStatus setPassword(@RequestBody Account acc) {

		try {
			long id = customerService.addPassword(acc.getCustomerId(), acc.getLoginPassword(), acc.getTransactionPassword());
			NewPasswordStatus status = new NewPasswordStatus();
			status.setStatus(true);
			status.setMessage("Password changes successful");
			status.setCustomerId(id);
			return status;
		}
		catch(ServiceException e) {
			NewPasswordStatus status = new NewPasswordStatus();
			status.setStatus(false);
			status.setMessage(e.getMessage());
			return status;
		}
	}

	@GetMapping("/accountSummary")
	public AccountSummary accountSummary(@RequestParam("accountNumber") long accountNumber) {

		try {
			AccountDetail detail = (AccountDetail) customerService.viewAccountDetails(accountNumber);

			AccountSummary acc = new AccountSummary();
			acc.setAccountNumber(detail.getAccountNumber());
			acc.setAccountType(detail.getAccountType());
			acc.setBankBalance(detail.getBankBalance());
			acc.setStatus(true);
			acc.setMessage("Customer is present");

			return acc;
		}
		catch(ServiceException e) {
			AccountSummary status = new AccountSummary();
			status.setStatus(false);
			status.setMessage(e.getMessage());	
			return status;
		}
	}
	@GetMapping("/getTransactionsByDate")
	public List<Transaction> getTransactionsOfCustomerByDate(@RequestParam("customerId") Long customerId,@RequestParam(value="fromDate")  String from, @RequestParam(value="toDate") String to ){
		try {
			CustomerRepository cust = new CustomerRepository(); 
			List<Transaction> list = customerService.fetchTransactionsByDate(customerId,from,to);
			Status status = new Status();
			status.setMessage("transactions fetched successfully !!");
			status.setStatus(true);
			return list;
		}
		catch(ServiceException e) {
			List<Transaction> list = new ArrayList<Transaction>();
			Status status = new Status();
			status.setMessage(e.getMessage());
			status.setStatus(true);
			return list;
		}
	}

	@GetMapping("/getTransactionsByMonth")
	public List<Transaction> getTransactionsBasedOnMonth(@RequestParam("customerId") Long customerId,String from , String to ){
			try {
			CustomerRepository cust = new CustomerRepository(); 
			List<Transaction> list = customerService.fetchTransactionsByMonth(customerId,from,to);
			Status status = new Status();
			status.setMessage("transactions fetched successfully !!");
			status.setStatus(true);
			return list;
		}
		catch(ServiceException e) {
			List<Transaction> list = new ArrayList<Transaction>();
			Status status = new Status();
			status.setMessage(e.getMessage());
			status.setStatus(true);
			return list;
		}
	}

	@GetMapping("/getAllTransactions")
	public List<Transaction> getAllTransactions(@RequestParam("custId") Long custId){
		try {
			CustomerRepository cust = new CustomerRepository(); 
			List<Transaction> list = customerService.fetchAllTransactions(custId);
			Status status = new Status();
			status.setMessage("transactions fetched successfully !!");
			status.setStatus(true);
			return list;
		}
		catch(ServiceException e) {
			List<Transaction> list = new ArrayList<Transaction>();
			Status status = new Status();
			status.setMessage(e.getMessage());
			status.setStatus(true);
			return list;
		}


	}

	@GetMapping("/addBeneficiary")
	public NewBeneficiaryStatus addNewBeneficiary(@RequestParam Long userAcc, Long beneAcc, String beneName, String nickName ) {

		try {
			customerService.addBeneficiary(userAcc, beneAcc, beneName, nickName);
			NewBeneficiaryStatus status = new NewBeneficiaryStatus();
			status.setStatus(true);
			status.setMessage("Beneficiary added");
			//status.setBeneficiaryAccountNumber(id);
			return status;
		}
		catch(ServiceException e) {
			NewBeneficiaryStatus status = new NewBeneficiaryStatus();
			status.setStatus(false);
			status.setMessage(e.getMessage());
			return status;
		}
	}
}
