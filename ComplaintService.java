package com.shopping.services;

import com.shopping.models.Complaint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ComplaintService — handles customer complaints.
 * Demonstrates CustomQueue (FIFO) for complaint processing.
 */
public class ComplaintService {

    private static ComplaintService instance;

    private Complaint[] complaints;
    private HashMap<Integer, Complaint> complaintMap;
    private CustomQueue<Complaint> complaintQueue;
    private int complaintCount;

    private ComplaintService() {
        this.complaints    = new Complaint[100];
        this.complaintMap  = new HashMap<>();
        this.complaintQueue = new CustomQueue<>();
        this.complaintCount = 0;
    }

    public static ComplaintService getInstance() {
        if (instance == null) instance = new ComplaintService();
        return instance;
    }

    /**
     * Submit new complaint — enqueues for processing — O(1).
     */
    public Complaint submitComplaint(int orderId, int customerId,
                                     String subject, String description) {
        try {
            Complaint c = new Complaint(orderId, customerId, subject, description);
            addComplaintInternal(c);
            complaintQueue.enqueue(c);
            return c;
        } catch (Exception e) {
            System.err.println("ComplaintService.submitComplaint error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Process next complaint from queue — O(1).
     */
    public Complaint processNextComplaint() {
        try {
            if (complaintQueue.isEmpty()) return null;
            Complaint c = complaintQueue.dequeue();
            c.setStatus(Complaint.ComplaintStatus.UNDER_REVIEW);
            return c;
        } catch (Exception e) {
            System.err.println("ComplaintService.processNextComplaint error: " + e.getMessage());
            return null;
        }
    }

    public void resolveComplaint(int complaintId, String resolution) {
        try {
            Complaint c = complaintMap.get(complaintId);
            if (c != null) {
                c.setStatus(Complaint.ComplaintStatus.RESOLVED);
                c.setResolution(resolution);
            }
        } catch (Exception e) {
            System.err.println("ComplaintService.resolveComplaint error: " + e.getMessage());
        }
    }

    public Complaint getComplaintById(int id) {
        try { return complaintMap.get(id); } catch (Exception e) { return null; }
    }

    public List<Complaint> getCustomerComplaints(int customerId) {
        try {
            List<Complaint> list = new ArrayList<>();
            for (int i = 0; i < complaintCount; i++)
                if (complaints[i] != null && complaints[i].getCustomerId() == customerId)
                    list.add(complaints[i]);
            return list;
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public List<Complaint> getPendingComplaints() {
        try {
            List<Complaint> list = new ArrayList<>();
            for (int i = 0; i < complaintCount; i++)
                if (complaints[i] != null && complaints[i].getStatus() == Complaint.ComplaintStatus.SUBMITTED)
                    list.add(complaints[i]);
            return list;
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public int getPendingComplaintsCount() { return complaintQueue.size(); }

    public List<Complaint> getAllComplaints() {
        try {
            List<Complaint> list = new ArrayList<>();
            for (int i = 0; i < complaintCount; i++) if (complaints[i] != null) list.add(complaints[i]);
            return list;
        } catch (Exception e) { return new ArrayList<>(); }
    }

    private void addComplaintInternal(Complaint c) {
        try {
            if (complaintCount >= complaints.length) {
                Complaint[] n = new Complaint[complaints.length * 2];
                System.arraycopy(complaints, 0, n, 0, complaintCount);
                complaints = n;
            }
            complaints[complaintCount++] = c;
            complaintMap.put(c.getComplaintId(), c);
        } catch (Exception e) {
            System.err.println("ComplaintService.addComplaintInternal error: " + e.getMessage());
        }
    }
}