package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * Hibernate entity bean representing a Task.
 * <p>
 * Conceptually task represents some work which is no further divided to smaller
 * pieces. It's work of a single person. A task is under a backlog item, which
 * is a bigger container of work.
 * <p>
 * Technically there's one-to-many relation between backlog item and a task. A
 * task has an effort estimate, as well as sum of work done. It has a creator
 * and an assignee. The Task also carries a log of events with it. Since one can
 * "watch" a task, watches are also tracked.
 * <p>
 * Task also is linked to some practices, but those are not currently
 * implemented in the UI.
 * <p>
 * Task is a unit which, within a Cycle of Control model, is in interest of
 * workers of a team, and sometimes their project manager also. Task is a
 * sub-part of a BacklogItem, and may be assigned to a named person.
 * <p>
 * Workers are interested in Tasks which have been assigned to them as things to
 * be done. To know better, which Task should be tackled next, there is a
 * priority attached to a Task. Task has a capability to log efforts done to it.
 * <p>
 * Project manager is generally more interested in BacklogItems than Tasks, but
 * in small projects, of for personal interests, may want to see the progress of
 * a single Task, too. Also, planning the future work to be assigned, it may be
 * useful for a Project manager to see the multitude of the tasks assigned to
 * each worker, to be able to balance the workload within her crew.
 */
@Entity
public class Task implements PageItem, Assignable, EffortContainer {

	private int id;

	private Priority priority;

	private TaskStatus status = TaskStatus.NOT_STARTED;

	private AFTime effortEstimate;

	private AFTime performedEffort;

	private String name;

	private String description;

	private BacklogItem backlogItem;

	private Date created;

	private User assignee;

	private User creator;

	private Collection<TaskEvent> events = new HashSet<TaskEvent>();

	private Map<Integer, User> watchers = new HashMap<Integer, User>();

	private Collection<PracticeAllocation> practices = new HashSet<PracticeAllocation>();

	@Type(type = "escaped_text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the id of this object.
	 * <p>
	 * The id is unique among all tasks.
	 */
	// tag this field as the id
	@Id
	// generate automatically
	@GeneratedValue(strategy = GenerationType.AUTO)
	// not nullable
	@Column(nullable = false)
	public int getId() {
		return id;
	}

	/**
	 * Set the id of this object.
	 * <p>
	 * You shouldn't normally call this.
	 */
	public void setId(int id) {
		this.id = id;
	}

	@Type(type = "escaped_truncated_varchar")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@ManyToOne
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	/** {@inheritDoc} */
	@ManyToOne
	public User getAssignee() {
		return assignee;
	}

	/** {@inheritDoc} */
	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	@OneToMany(mappedBy = "task")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy(value = "created")
	public Collection<TaskEvent> getEvents() {
		return events;
	}

	public void setEvents(Collection<TaskEvent> events) {
		this.events = events;
	}

	@Type(type = "af_time")
	public AFTime getEffortEstimate() {
		return effortEstimate;
	}

	@Type(type = "af_time")
	@Formula(value = "(select SUM(e.effort) from TaskEvent e "
			+ "where e.eventType = 'PerformedWork' and e.task_id = id)")
	public AFTime getPerformedEffort() {
		return performedEffort;
	}

	protected void setPerformedEffort(AFTime performedEffort) {
		this.performedEffort = performedEffort;
	}

	public void setEffortEstimate(AFTime effortEstimate) {
		this.effortEstimate = effortEstimate;
	}

	@ManyToOne
	@JoinColumn(nullable = false)
	public BacklogItem getBacklogItem() {
		return backlogItem;
	}

	public void setBacklogItem(BacklogItem backlogItem) {
		this.backlogItem = backlogItem;
	}

	/** {@inheritDoc} */
	@Transient
	public Collection<PageItem> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Transient
	public PageItem getParent() {
		return getBacklogItem();
	}

	/** {@inheritDoc} */
	@Transient
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
			@Parameter(name = "useOrdinal", value = "true"),
			@Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.Priority") })
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	@Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
			@Parameter(name = "useOrdinal", value = "true"),
			@Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.TaskStatus") })
	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	/** Set task to use a template. Not currently in use. */
	public void useTemplate(PracticeTemplate template) {

		ArrayList<PracticeAllocation> practiceAllocations = new ArrayList<PracticeAllocation>();

		for (Practice p : template.getPractices()) {
			practiceAllocations.add(new PracticeAllocation(p, this));
		}

		setPractices(practiceAllocations);
	}

	@OneToMany(mappedBy = "task")
	public Collection<PracticeAllocation> getPractices() {
		return practices;
	}

	public void setPractices(Collection<PracticeAllocation> practices) {
		this.practices = practices;
	}

	@ManyToMany()
	@MapKey()
	public Map<Integer, User> getWatchers() {
		return watchers;
	}

	public void setWatchers(Map<Integer, User> watchers) {
		this.watchers = watchers;
	}
}