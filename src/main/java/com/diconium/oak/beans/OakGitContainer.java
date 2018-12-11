package com.diconium.oak.beans;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import java.sql.Blob;

@Getter
@Setter
public class OakGitContainer {

	private String ID = StringUtils.EMPTY;

	private long MODIFIED = 0L;

	private short HASBINARY = 0;

	private short DELETEDONCE = 0;

	private long MODCOUNT = 0L;

	private long CMODCOUNT = 0L;

	private long DSIZE = 0L;

	private short VERSION = 0;

	private short SDTYPE = 0;

	private long SDMAXREVTIME = 0L;

	private String DATA = StringUtils.EMPTY;

	private Blob BDATA;
	
	
	

}
