package com.lti.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;



@Entity
@Table(name="tbl_account_detail")
public class Account {

	@Id	
	@Column(name="customer_id")
	private long customerId;
	
	@OneToOne
	@JoinColumn(name="service_reference_no")
	private AcceptedRegistrations registration ;
	
	@Column(name="login_password")
	private String loginPassword;
	
	@Column(name="transaction_password")
	private String transactionPassword;
	
	@OneToMany(mappedBy = "account",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<AccountDetail> accounts;

	@OneToOne(mappedBy = "account",cascade = CascadeType.ALL)
	private GeneralDetail generalDetail;
	
	
	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
		

	public AcceptedRegistrations getRegistration() {
		return registration;
	}

	public void setRegistration(AcceptedRegistrations registration) {
		this.registration = registration;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getTransactionPassword() {
		return transactionPassword;
	}

	public void setTransactionPassword(String transactionPassword) {
		this.transactionPassword = transactionPassword;
	}

	public List<AccountDetail> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountDetail> accounts) {
		this.accounts = accounts;
	}

	public GeneralDetail getGeneralDetail() {
		return generalDetail;
	}

	public void setGeneralDetail(GeneralDetail generalDetail) {
		this.generalDetail = generalDetail;
	}
	
	
}
