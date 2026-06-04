package org.springframework.samples.petclinic.feedback

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class JdbcClientFeedbackRepository(
	private val jdbcClient: JdbcClient,
	private val jdbcTemplate: JdbcTemplate
) : FeedbackRepository {

	private val feedbackRowMapper = { rs: java.sql.ResultSet, _: Int ->
		val feedback = Feedback()
		feedback.id = rs.getInt("id")
		feedback.name = rs.getString("name")
		feedback.email = rs.getString("email")
		feedback.message = rs.getString("message")
		feedback.createdAt = rs.getTimestamp("created_at").toLocalDateTime()
		feedback
	}

	@Transactional
	override fun save(feedback: Feedback): Feedback {
		if (feedback.isNew()) {
			val keyHolder = org.springframework.jdbc.support.GeneratedKeyHolder()
			jdbcTemplate.update({ con ->
				val ps = con.prepareStatement(
					"INSERT INTO feedback (name, email, message, created_at) VALUES (?, ?, ?, ?)",
					java.sql.Statement.RETURN_GENERATED_KEYS
				)
				ps.setString(1, feedback.name)
				ps.setString(2, feedback.email)
				ps.setString(3, feedback.message)
				ps.setTimestamp(4, java.sql.Timestamp.valueOf(feedback.createdAt))
				ps
			}, keyHolder)
			feedback.id = keyHolder.getKey()!!.toInt()
		}
		return feedback
	}

	@Transactional(readOnly = true)
	override fun findAll(): List<Feedback> {
		return jdbcClient.sql("SELECT id, name, email, message, created_at FROM feedback ORDER BY created_at DESC")
			.query(feedbackRowMapper)
			.list()
	}

}