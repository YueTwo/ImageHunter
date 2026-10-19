# ImageHunter

[![中文文档](https://img.shields.io/badge/语言-中文-d73a49?style=flat-square)](README_ZH.md)

Phones make it effortless to record everyday life, but they also turn photos and videos into a vast personal archive. Untitled moments can quietly disappear into it, simply because they are hard to find. ImageHunter is designed to help every memory worth keeping be seen again when it matters.

For images and videos, the system offers independent visual-description generation and natural-language retrieval. The former turns visual information into text; the latter matches a user's query directly to media content, without requiring prior annotations or generated descriptions. Available on Android and the web, ImageHunter combines synchronized personal accounts, cloud storage, and cross-device management to help users organize, preserve, and revisit their digital memories.

## Awards & Recognition

### Provincial Award

**Second Prize — Provincial Selection, Elite Division I**
The 5th Global Campus Artificial Intelligence Algorithm Elite Competition (2023)

<img src="figures/rewards/第五届全球校园人工智能算法精英大赛省级二等奖.png" alt="2023 provincial second-prize certificate" width="46%">

### National Award

**Second Prize — National Final**
The 5th Global Campus Artificial Intelligence Algorithm Elite Competition (2023)

<img src="figures/rewards/第五届全球校园人工智能算法精英大赛全国二等奖.png" alt="2023 national second-prize certificate" width="46%">

## System Overview

<img src="figures/framework.png" alt="ImageHunter system architecture: Android and web clients connect through Nginx and CMS to model processing, object storage, and the database" width="100%">

### How ImageHunter Runs

1. A user signs in on Android or the web and receives a time-limited authentication token.
2. For uploads, retrieval, or description generation, Nginx validates the token and routes the authorized request to CMS.
3. CMS coordinates object storage and database updates, and invokes the model service when media analysis is requested.
4. The web service returns the processing result to the client. Description generation and retrieval are separate model capabilities.

## Mobile App

The mobile app is the primary experience for uploading and managing media, generating descriptions, and retrieving content.

### Home & Upload

The home screen provides a direct upload entry and clear access to description generation and search. Users can upload image or video files from the device.

<img src="figures/mobile/main_interface.png" alt="Mobile splash and home screens" width="72%">

### Natural-Language Retrieval

Describe the content in plain language, select image or video, choose local or cloud computation, and optionally narrow the time range. The retrieval capability independently matches the query to media content and returns results with confidence scores.

<img src="figures/mobile/search_interface.png" alt="Mobile search workflow and results" width="100%">

### Media Description Generation

Take or select an image or video, choose its type, and generate a text description with one tap.

<img src="figures/mobile/generate_interface.png" alt="Mobile media description generation" width="72%">

### Personal Space

The personal area brings together daily check-in, cloud storage, privacy information, and account controls.

<img src="figures/mobile/user_interface.png" alt="Mobile personal space" width="38%">

## Web App

The web app offers a streamlined desktop complement to the mobile workflow.

### Home

Landing page and navigation.

<img src="figures/web/main_page.png" alt="Web home page" width="72%">

### Search

Text-based media results.

<img src="figures/web/search_page.png" alt="Web search results" width="72%">

### Generate

Upload media by type.

<img src="figures/web/generate_page.png" alt="Web generation page" width="72%">

### Personal Space

Browse retrieval history.

<img src="figures/web/user_home.png" alt="Web personal space" width="72%">

---

[阅读中文版 →](README_ZH.md)
