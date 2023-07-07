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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.pms.constants.PMSEntityConstants;
import com.pms.constants.PMSPriority;
import com.pms.constants.PMSTaskStatus;

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
public class PMSTask {
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="ID_Gen")
    @Column(name="ID")
    @Setter(AccessLevel.NONE)
    private Long id;
    
    @Column(name="NAME", nullable=false)
    @Size(min=PMSEntityConstants.kMinTaskNameLen, 
    	max=PMSEntityConstants.kMaxTaskNameLen, 
    	message="name should between [{min}, {max}]")
    private String name;

    @Lob
    @Column(name = "DESCRIPTION", columnDefinition="TEXT")
    private String desc;
    
    @Column(name = "PROJECT")
    private Long projectId;
    
    @OneToOne
    private PMSFile avatar;
    
    @ElementCollection
    @CollectionTable(name="TASK_DEPENDENT_TASKS")
    private List<Long> dependentTaskIds;
    
    @ElementCollection
    @CollectionTable(name="TASK_USERS")
    private List<Long> userIds;
    
    @ElementCollection
    @CollectionTable(name="TASK_COMMENTS")
    private List<Long> commentIds;
    
    @Column(name="START_DATE", nullable=false)
    private Long startDate;
    
    @Column(name="END_DATE", nullable=false)
    private Long endDate;
    
    @OneToMany
    private List<PMSTag> tags;
    
    @Enumerated(EnumType.STRING)
    private PMSPriority priority;
    
    @Enumerated(EnumType.STRING)
    private PMSTaskStatus status;
    
    @OneToMany
    private List<PMSFile> attachments;
    
    public PMSTask() {
    	dependentTaskIds = new ArrayList<>();
    	userIds = new ArrayList<>();
        commentIds = new ArrayList<>();
        attachments = new ArrayList<>();
        tags = new ArrayList<>();
        priority = PMSPriority.Normal;
        status = PMSTaskStatus.NotStarted;
        
        // set project id of the default task to -1, 
        // as the relationship is managed in column pmsproject_default_task in table pmsproject
        projectId = PMSEntityConstants.kDefaultTaskProjectId; 
        
        startDate = System.currentTimeMillis();
        endDate = System.currentTimeMillis();
    }
    
    public void addCommentId(Long commentId) {
        if (!commentIds.contains(commentId)) {
            commentIds.add(commentId);
        }
    }
    
    public void removeCommentId(Long commentId) {
        if (commentIds.contains(commentId)) {
            commentIds.remove(commentId);
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
    
    public void addDependentTaskId(Long taskId) {
        if (!dependentTaskIds.contains(taskId)) {
        	dependentTaskIds.add(taskId);
        }
    }
    
    public void removeDependentTaskId(Long taskId) {
        if (dependentTaskIds.contains(taskId)) {
        	dependentTaskIds.remove(taskId);
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
