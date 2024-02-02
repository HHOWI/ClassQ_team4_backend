package com.kh.elephant.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@DynamicInsert
@Builder
@Entity
public class PostAttachments {

    @Id
    @Column(name = "post_attachment_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "postAttachmentSequence")
    @SequenceGenerator(name="postAttachmentSequence", sequenceName = "SEQ_POST_ATTACHMENT",allocationSize = 1)
    private int postAttachmentSEQ;

    @Column(name = "post_seq")
    private int postCode;

    @Column(name = "attachment_url")
    private String attachmentURL;
}
