package com.leige.demo1.ModelVo;

import com.leige.demo1.model.Clause;
import com.leige.demo1.model.ControlRequirement;

import java.util.List;

/**
 * @author peichanglei
 * @date 2018/9/21 14:41
 */
public class ClauseVo extends Clause {
    private List<ClauseVo> clauseList;
    private List<ControlRequirement> controlRequirementList;

    public List<ClauseVo> getClauseList() {
        return clauseList;
    }

    public void setClauseList(List<ClauseVo> clauseList) {
        this.clauseList = clauseList;
    }

    public List<ControlRequirement> getControlRequirementList() {
        return controlRequirementList;
    }

    public void setControlRequirementList(List<ControlRequirement> controlRequirementList) {
        this.controlRequirementList = controlRequirementList;
    }
}
