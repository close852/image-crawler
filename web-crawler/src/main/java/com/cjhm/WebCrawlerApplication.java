package com.cjhm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebCrawlerApplication {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Whale/1.5.72.5 Safari/537.36";
	private static final String PATH = "D:\\git\\practice\\web-crawler\\src\\main\\resources";

		
	public static void main(String[] args) {
		SpringApplication.run(WebCrawlerApplication.class, args).close();
	}

	@Bean
	public CommandLineRunner init() {
		return (args) -> {
			newWebtoon("641253");
		};
	}
	
	public void newWebtoon(String webtoonName) throws IOException {
		makeWebtoonImage(webtoonName);
	}

	public void batchWebToon() {
		List<String> webToonList = new ArrayList<String>();

		for (int i = 0; i < webToonList.size(); i++) {
			String webtoonName = webToonList.get(i);
			try {
				allThisWebtoon(webtoonName);
			} catch (IOException e) {

			}
		}
	}

	public void batchWebToon(List<String> webToonList) {

		for (int i = 0; i < webToonList.size(); i++) {
			String webtoonName = webToonList.get(i);
			try {
				allThisWebtoon(webtoonName);
			} catch (IOException e) {

			}
		}
	}
	
	public void allThisWebtoon(String webtoonName) throws IOException {
		int max = Integer.MAX_VALUE;
		max = 3;
		System.out.println("> "+webtoonName);
		for (int i = 1; i <= max; i++) {
			try {
				System.out.println(i + "화 다운로드...");
				makeWebtoonImage(webtoonName, i);
				System.out.println(i + "화 다운로드 완료");
			} catch (IndexOutOfBoundsException e) {
				System.out.println(i + "화 다운로드 종료");
				System.out.println(webtoonName + " 완료");
				break;
			}
		}
	}

	private void makeWebtoonImage(String webtoonName) throws IOException {
		makeWebtoonImage(webtoonName,0);
	}
	
	private void makeWebtoonImage(String webtoonName, int no) throws IOException {
		if (no <= 0) {
			System.out.println(webtoonName + " : 최신화 다운로드");
		}
		String connUrl = "https://comic.naver.com/webtoon/detail.nhn?titleId=" + webtoonName + "&no=" + no;
		connUrl = "https://comic.naver.com/webtoon/detail.nhn?titleId=" + webtoonName;
		System.out.println(connUrl);
		Connection conn = Jsoup.connect(connUrl)
							.header("Content-Type", "application/json;charset=UTF-8")
							.header("Accept", "*/*")
							.userAgent(USER_AGENT)
							.ignoreContentType(true)
							.method(Method.GET);
		Document doc = conn.get();
		Elements imgs = doc.body().getElementsByClass("wt_viewer").get(0).getElementsByTag("img");
		for (Element e : imgs) {
			String imgSrc = e.attr("src");

			if (!e.hasAttr("alt") || !"comic content".equals(e.attr("alt"))) {
				continue;
			}
			saveImage(imgSrc);
		}

	}

	//image 다운로드
	public void saveImage(String strUrl) {

		URL url = null;
		URLConnection uc = null;
		InputStream in = null;
		OutputStream os = null;

		try {
			url = new URL(strUrl);

			String path = strUrl.replace("https://image-comic.pstatic.net", "");
			File newFile = new File(PATH + path);
			mkUpDirs(newFile.getAbsolutePath());

			if (newFile.exists()) {
				System.out.println("file exists.. " + newFile.getName());
				return;
			}

			os = new FileOutputStream(newFile);
			uc = url.openConnection();
			uc.addRequestProperty("User-Agent", USER_AGENT);
			uc.addRequestProperty("Content-Type", "image/jpeg");

			in = uc.getInputStream();
			int len = 0;
			byte[] buf = new byte[1024];
			while ((len = in.read(buf)) != -1) {
				os.write(buf, 0, len);
			}
			os.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void mkUpDirs(String absPath) {
		int lastIdx = absPath.lastIndexOf("\\");
		File dir = new File(absPath.substring(0, lastIdx));
		if(!dir.exists()) {
			dir.mkdirs();
		}
	}
}
