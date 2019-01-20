package com.share.lifetime.common.support.netty.client;

import org.junit.Test;

import com.share.lifetime.common.support.netty.RequestData;
import com.share.lifetime.common.support.netty.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientTest {

    @Test
    public void testSendRequest() throws InterruptedException {
        RequestData requestData = new RequestData();
        requestData.setValue(
            "First, I want to tell you how proud we are. Getting into Columbia is a real testament of what a great well-rounded student you are. Your academic, artistic, and social skills have truly blossomed in the last few years. Whether it is getting the highest grade in Calculus, completing your elegant fashion design, successfully selling your painted running shoes, or becoming one of the top orators in Model United Nations, you have become a talented and accomplished young woman. You should be as proud of yourself as we are."
                + "I will always remember the first moment I held you in my arms. I felt a tingling sensation that directly touched my heart. It was an intoxicating feeling I will always have. It must be that \"father-daughter connection\" which will bind us for life. I will always remember singing you lullaby while I rocked you to sleep. When I put you down, it was always with both relief (she finally fell asleep!) and regret (wishing I could hold you longer). And I will always remember taking you to the playground, and watching you having so much fun. You were so cute and adorable, and that is why everybody loved you so."
                + "You have been a great kid ever since you were born, always quiet, empathetic, attentive, and well-mannered. You were three when we built our house. I remember you quietly followed us every weekend for more than ten hours a day to get building supplies. You put up with that boring period without a fuss, happily ate hamburgers every meal in the car, sang with Barney until you fell asleep. When you went to Sunday Chinese school, you studied hard even though it was no fun for you. I cannot believe how lucky we are as parents to have a daughter like you."
                + "You have been an excellent elder sister. Even though you two had your share of fights, the last few years you have become best friends. Your sister loves you so much, and she loves to make you laugh. She looks up to you, and sees you as her role model. As you saw when we departed, she misses you so much. And I know that you miss her just as much. There is nothing like family, and other than your parents, your sister is the one person who you can trust and confide in. She will be the one to take care of you, and the one you must take care of. There is nothing we wish more than that your sisterhood will continue to bond as you grow older, and that you will take care of each other throughout your lives. For the next four years, do have a short video chat with her every few days, and do email her when you have a chance."
                + "College will be the most important years in your life. It is in college that you will truly discover what learning is about. You often question \"what good is this course\". I encourage you to be inquisitive, but I also want to tell you: \"Education is what you have left after all that is taught is forgotten.\" What I mean by that is the materials taught isn't as important as you gaining the ability to learn a new subject, and the ability to analyze a new problem. That is really what learning in college is about – this will be the period where you go from teacher-taught to master-inspired, after which you must become self-learner. So do take each subject seriously, and even if what you learn isn't critical for your life, the skills of learning will be something you cherish forever."
                + "Do not fall into the trap of dogma. There is no single simple answer to any question. Remember during your high school debate class, I always asked you to take on the side that you don't believe in? I did that for a reason -- things rarely \"black and white\", and there are always many ways to look at a problem. You will become a better problem solver if you recognized that. This is called \"critical thinking\", and it is the most important thinking skill you need for your life. This also means you need to become tolerant and supportive of others. I will always remember when I went to my Ph.D. advisor and proposed a new thesis topic. He said \"I don't agree with you, but I'll support you.\" After the years, I have learned this isn't just flexibility, it is encouragement of critical thinking, and an empowering style of leadership, and it has become a part of me. I hope it will become a part of you too."
                + "Follow your passion in college. Take courses you think you will enjoy. Don't be trapped in what others think or say. Steve Jobs says when you are in college, your passion will create many dots, and later in your life you will connect them. In his great speech given at Stanford commencement, he gave the great example where he took calligraphy, and a decade later, it became the basis of the beautiful Macintosh fonts, which later ignited desktop publishing, and brought wonderful tools like Microsoft Word to our lives. His expedition into calligraphy was a dot, and the Macintosh became the connecting line. So don't worry too much about what job you will have, and don't be too utilitarian, and if you like Japanese or Korean, go for it, even if your dad thinks \"it's not useful\" : ) Enjoy picking your dots, and be assured one day you will find your calling, and connect a beautiful curve through the dots."
                + "珍惜你的大学时光吧，好好利用你的空闲时间，成为掌握自己命运的独立思考者，发展自己的多元化才能，大胆地去尝试，通过不断的成功和挑战来学习和成长，成为融汇中西的人才。");
        int length = requestData.getValue().getBytes().length;
        log.info("length:{}", length);
         NettyClient client = new NettyClient("localhost", 8181, requestData);
        ResponseData sendRequest = client.sendRequest();
        log.info("{}", sendRequest);
    }

}
