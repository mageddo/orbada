create index orep_group_orepg_fk_i on orep_groups(orepg_orepg_id)
/
create index orep_group_dtp_fk_i on orep_groups(orepg_dtp_id)
/
create index orep_group_usr_fk_i on orep_groups(orepg_usr_id)
/
create index orep_group_sch_fk_i on orep_groups(orepg_sch_id)
/
create index orep_report_orepg_fk_i on orep_reports (orep_orepg_id)
/
create index orep_report_orep_fk_i on orep_reports (orep_orep_id)
/
create index orep_user_orep_fk_i on orep_users (orep_orep_id)
/
create index orep_user_usr_fk_i on orep_users (orep_usr_id)
/
