package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.WorkType;

public class WorkTypeAction extends ActionSupport implements CRUDAction {

	private static final long serialVersionUID = -7854997077386161741L;

	private int workTypeId;

	private int activityTypeId;

	private ActivityType activityType;

	private WorkType workType;

	private WorkTypeDAO workTypeDAO;

	private ActivityTypeDAO activityTypeDAO;

	private int storedWorkTypeId;

	public String create() {
		activityType = activityTypeDAO.get(activityTypeId);
		if (activityType == null) {
			super
					.addActionError(super
							.getText("workType.activityTypeNotFound"));
			return Action.ERROR;
		}
		this.workTypeId = 0;
		this.workType = new WorkType();
		return Action.SUCCESS;
	}

	public String edit() {
		activityType = activityTypeDAO.get(activityTypeId);
		if (activityType == null) {
			super
					.addActionError(super
							.getText("workType.activityTypeNotFound"));
			return Action.ERROR;
		}
		workType = workTypeDAO.get(workTypeId);
		if (workType == null) {
			super.addActionError(super.getText("wotkType.notFound"));
			return Action.INPUT;
		}
		return Action.SUCCESS;
	}

	public String delete() {
		workType = workTypeDAO.get(workTypeId);
		if (workType == null) {
			super.addActionError(super.getText("wotkType.notFound"));
			return Action.INPUT;
		}
		workTypeDAO.remove(workType);
		return Action.SUCCESS;
	}

	public String store() {
		if (workType == null) {
			super.addActionError(super.getText("workType.missingForm"));
			return Action.INPUT;
		}
		activityType = activityTypeDAO.get(activityTypeId);
		if (activityType == null) {
			super
					.addActionError(super
							.getText("workType.activityTypeNotFound"));
			return Action.INPUT;
		}
		WorkType fillable = new WorkType();
		if (workTypeId > 0) {
			fillable = workTypeDAO.get(workTypeId);
			if (workType == null) {
				super.addActionError(super.getText("workType.notFound"));
				return Action.INPUT;
			}
		}
		fillObject(fillable);
		fillable.setActivityType(activityType);
		workTypeDAO.store(fillable);

		storedWorkTypeId = fillable.getId();

		return Action.SUCCESS;
	}

	protected void fillObject(WorkType fillable) {
		fillable.setActivityType(this.activityType);
		fillable.setDescription(this.workType.getDescription());
		fillable.setName(this.workType.getName());
	}

	public int getActivityTypeId() {
		return activityTypeId;
	}

	public void setActivityTypeId(int activityTypeId) {
		this.activityTypeId = activityTypeId;
	}

	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public int getWorkTypeId() {
		return workTypeId;
	}

	public void setWorkTypeId(int workTypeId) {
		this.workTypeId = workTypeId;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityTypeDAO(ActivityTypeDAO activityTypeDAO) {
		this.activityTypeDAO = activityTypeDAO;
	}

	public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
		this.workTypeDAO = workTypeDAO;
	}

	public int getStoredWorkTypeId() {
		return storedWorkTypeId;
	}
}
