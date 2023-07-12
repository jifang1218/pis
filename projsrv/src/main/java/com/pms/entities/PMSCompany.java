/**
 * 
 */
package com.pms.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.pms.constants.PMSEntityConstants;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * @author jifang
 *
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class PMSCompany {
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="ID_Gen")
    @Column(name="ID")
    @Setter(AccessLevel.NONE)
    private Long id;
    
    @Column(name="NAME", nullable=false, unique=true)
    @NotNull(message="name should be unique and not null. ")
    @Size(min=PMSEntityConstants.kMinCompanyNameLen, max=PMSEntityConstants.kMaxCompanyNameLen, message="Length of name should be between [{min}, {max}]. ")
    private String name;

    @Lob 
    @Column(name="DESCRIPTION", columnDefinition="TEXT")
    private String desc;
    
    @Column(name="ADDRESS")
    private String address;
    
    @Column(name="PHONE")
    private String phone;
    
    @Column(name="WEBSITE")
    private String website;
    
    @OneToOne
    private PMSFile avatar;
    
    @ElementCollection
    @CollectionTable(name="COMPANY_PROJECTS")
    private List<Long> projectIds;
    
    @ElementCollection
    @CollectionTable(name="COMPANY_USERS")
    private List<Long> userIds;
    
    public PMSCompany() {
        projectIds = new ArrayList<>();
        userIds = new ArrayList<>();
    }
    
    public void addProjectId(Long projectId) {
        if (!projectIds.contains(projectId)) {
            projectIds.add(projectId);
        }
    }
    
    public void removeProjectId(Long projectId) {
        if (projectIds.contains(projectId)) {
            projectIds.remove(projectId);
        }
    }
    
    public void addUserId(Long userId) {
    	if (!userIds.contains(userId)) {
    		userIds.add(userId);
    	}
    }
    
    public void removeUserId(Long userId) {
    	if (userIds.contains(userId)) {
    		userIds.remove(userId);
    	}
    }
    
    @CreatedBy
    @Column(updatable=false)
    private String createdBy;
    
    @CreatedDate
    @Column(updatable=false)
    private Long createdTime;
    
    @LastModifiedBy
    private String modifiedBy;
    
    @LastModifiedDate
    private Long modifiedTime;
}
