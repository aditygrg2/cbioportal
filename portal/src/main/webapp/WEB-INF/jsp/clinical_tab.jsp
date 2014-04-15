<%@ page import="org.json.simple.JSONObject"%>

<style>
    #survival .survival_stats_table {
        margin-top: 10px;
        margin-bottom: 30px;
        margin-left: 95px;
        width: 620px;
        background-color: #FEFFC5;
        height: 80px;
        width: 720px;
    }
    #survival td{
        width: 140px;
        font-size: 13px;
        font-family: Arial, Helvetica, sans-serif;
        text-align: center;
        border: 1px solid #D8D8D8;
    }
    #survival h4{
        margin-left: 60px;
        margin-top: 20px;
        font-size: 150%;
        height: 30px;
    }
    #survival .img_buttons{
        font-size: 13px;
        display: inline;
        padding-left: 5px;
    }
</style>

<script>
    var cancer_study_id = "<%out.print(cancer_study_id);%>",
            case_set_id = "<%out.print(patient_set_id);%>";
    var case_ids_key = "";
    if (case_set_id === "-1") {
        case_ids_key = "<%out.print(patientIdsKey);%>";
    }
</script>
<script type="text/javascript" src="js/src/survival_curve.js"></script>

<div class="section" id="survival">
    <h4 id='os_header'>Overall Survival Kaplan-Meier Estimate</h4>
    <div id="os_survival_curve"></div>
    <div class="survival_stats_table" id="os_stat_table"></div>
    <h4 id='dfs_header'>Disease Free Survival Kaplan-Meier Estimate</h4>
    <div id="dfs_survival_curve"></div>
    <div class="survival_stats_table" id="dfs_stat_table"></div>
</div>

<script>
    function getSurvivalPlotsPatientList() {
        <%
            JSONObject result = new JSONObject();
            for (String patientId : mergedPatientList) {
                if (dataSummary.isCaseAltered(patientId)) {
                    result.put(patientId, "altered");
                } else {
                    result.put(patientId, "unaltered");
                }
            }
        %>
        var obj = jQuery.parseJSON('<%=result%>');
        return obj;
    }

    $(document).ready(function() {
        survivalCurves.init(getSurvivalPlotsPatientList());
    });
</script>