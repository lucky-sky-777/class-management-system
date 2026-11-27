package com.mezon.classmanagement.backend.domain_document.main.recommendation.projection;

public interface DocumentClusterMembershipProjection {

	Long getDocumentId();

	Integer getClusterIndex();

	Double getMembership();

}