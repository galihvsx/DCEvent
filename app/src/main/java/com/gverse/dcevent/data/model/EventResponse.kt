package com.gverse.dcevent.data.model

import com.google.gson.annotations.SerializedName

data class EventResponse(

	@field:SerializedName("listEvents")
	val listEvents: List<Event?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)