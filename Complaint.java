package com.shopping.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ComplaintStatus { SUBMITTED, OPEN, UNDER_REVIEW, RESOLVED, DISMISSED }

    private String          complaintId;
    private int             customerId;
    private String          customerName;
    private String          orderId;
    private String          subject;
    private String          description;
    private ComplaintStatus status;
    private String          submittedDate;
    private String          resolvedDate;
    private String          resolution;

    public Complaint() {
        status        = ComplaintStatus.SUBMITTED;
        submittedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Complaint(String complaintId, int customerId, String customerName,
                     String orderId, String subject, String description) {
        this();
        this.complaintId  = complaintId;
        this.customerId   = customerId;
        this.customerName = customerName;
        this.orderId      = orderId;
        this.subject      = subject;
        this.description  = description;
    }

    public String          getComplaintId()  { return complaintId; }
    public int             getCustomerId()   { return customerId; }
    public String          getCustomerName() { return customerName; }
    public String          getOrderId()      { return orderId; }
    public String          getSubject()      { return subject; }
    public String          getDescription()  { return description; }
    public ComplaintStatus getStatus()       { return status; }
    public String          getSubmittedDate(){ return submittedDate; }
    public String          getResolvedDate() { return resolvedDate; }
    public String          getResolution()   { return resolution; }

    public void setComplaintId(String v)         { complaintId  = v; }
    public void setCustomerId(int v)             { customerId   = v; }
    public void setCustomerName(String v)        { customerName = v; }
    public void setOrderId(String v)             { orderId      = v; }
    public void setSubject(String v)             { subject      = v; }
    public void setDescription(String v)         { description  = v; }
    public void setStatus(ComplaintStatus v)     { status       = v; }
    public void setSubmittedDate(String v)       { submittedDate= v; }
    public void setResolvedDate(String v)        { resolvedDate = v; }
    public void setResolution(String v)          { resolution   = v; }

    public void resolve(String res) {
        resolution   = res;
        status       = ComplaintStatus.RESOLVED;
        resolvedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}