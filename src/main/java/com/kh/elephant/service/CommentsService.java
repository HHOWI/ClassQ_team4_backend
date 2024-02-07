package com.kh.elephant.service;

import com.kh.elephant.domain.Comments;
import com.kh.elephant.domain.QComments;
import com.kh.elephant.repo.CommentsDAO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsService {

    @Autowired
    private CommentsDAO dao;

    @Autowired(required = true)
    private JPAQueryFactory queryFactory;

    private final QComments qComments = QComments.comments;

    public List<Comments> showAll() {
        return dao.findAll();
    }

    public Comments show(int code) {
        return dao.findById(code).orElse(null);
    }

    public Comments create(Comments comments) {
        return dao.save(comments);
    }

    public Comments update(Comments comments) {
        return  dao.save(comments);
    }

    public Comments delete(Comments comments) {
        return  dao.save(comments);
    }

    // 게시물 1개에 따른 댓글 전체 조회
    public List<Comments> findByPostSeq(int id) {
        return dao.findByPostSeq(id);
    }

    public List<Comments> getAllTopLevelComments(int postSeq) {
        return queryFactory.selectFrom(qComments)
                .where(qComments.parent.isNull())
                .where(qComments.post.eq(postSeq))
                .orderBy(qComments.commentDate.desc())
                .fetch();
    }

    public List<Comments> getRepliesByCommentId(Integer parentId, int postSeq) {
        return queryFactory.selectFrom(qComments)
                .where(qComments.commentsParentSeq.eq(parentId))
                .where(qComments.post.eq(postSeq))
                .orderBy(qComments.commentDate.asc())
                .fetch();
    }

    public List<Comments> findCommentsByUserId(String userId) { return dao.findCommentsByUserId(userId); }

    // 자식 댓글 같이 삭제
    public void deleteParentAndChildren(int parentCommentsSEQ) {
        List<Comments> children = dao.findByCommentsParentSeq(parentCommentsSEQ);
        for (Comments child : children) {
            child.setCommentDelete("Y");
            dao.save(child);
        }
        Comments parent = dao.findById(parentCommentsSEQ).orElse(null);
        if (parent != null) {
            dao.delete(parent);
        }
    }
}
