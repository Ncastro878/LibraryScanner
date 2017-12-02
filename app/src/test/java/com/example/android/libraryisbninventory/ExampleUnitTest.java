package com.example.android.libraryisbninventory;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRule;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {


    @Mock
    BookResultMVP.View view;
    BookResultPresenter presenter;

    /**
     * This method will be called before each of the tests.
     * This will setup fields and keep it DRY.
     */
    @Before
    public void setUp() throws Exception {
        presenter = new BookResultPresenter(view);
    }

    @Test
    public void view_get_img_url_returns_passed_in_string(){
        //given
        //view = new MockView();
        Mockito.when(view.getImgUrl()).thenReturn("FakeString");

        //when
        presenter.setViewImgUrl("FakeString");
        view.getImgUrl();

        //then
        //oldTest = Assert.assertEquals("FakeString", view.getImgUrl());
        //This is a new behavior test and checks whether the mock Object called said function.
        Mockito.verify(view).getImgUrl();
    }
    @Test
    public void view_get_img_url_returns_null_when_no_string_passed_in(){
        //given
        Mockito.when(view.getImgUrl()).thenReturn(null);
        //when

        //then
        Assert.assertEquals(null, view.getImgUrl());
    }

    /**
     * This is a manual Mock, but we don't need to do this.
     * We can use mockito to make mocks for us! Coming up!
     * Leaving for posterity.
     */
    private class MockView implements BookResultMVP.View{
        String imgUrl = "FakeImgUrl";
        @Override
        public void setBookViews(BookInfoObject bookInfo) {}
        @Override
        public String getBookTextViewTitle() {
            return null;
        }
        @Override
        public String getBookTextViewAuthor() {
            return null;}
        @Override
        public String getImgUrl() {
            return imgUrl;}
        //test
        @Override
        public void setImgUrl(String url) {
            imgUrl = url;
        }
    }
}