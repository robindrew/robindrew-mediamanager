package com.robindrew.mediamanager.component.tag;

import java.util.Set;

public interface ITagCache {

	Set<ITag> getTags();

	ITag getTag(String name);

	ITag getTag(int number);

}
