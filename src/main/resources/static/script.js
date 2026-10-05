/* =========================================================
   RESUME ANALYZER - FINAL SCRIPT
========================================================= */

"use strict";

/* =========================================================
   CONFIGURATION
========================================================= */

const API_BASE_URL = "";

const LOGIN_URL = "/api/auth/login";
const REGISTER_URL = "/api/auth/register";
const LOGOUT_URL = "/api/auth/logout";

const UPLOAD_URL = "/api/resumes/upload";
const RESUMES_URL = "/api/resumes/my-resumes";

const JOBS_URL = "/api/job-descriptions";
const JOB_MATCH_URL = "/api/job-match";

/* =========================================================
   GLOBAL VARIABLES
========================================================= */

let currentUser = null;
let currentResumeId = null;
let currentResume = null;
let currentMatches = [];

/* =========================================================
   BASIC HELPERS
========================================================= */

function getElement(id) {
    return document.getElementById(id);
}

function setText(id, value) {
    const element = getElement(id);

    if (element) {
        element.textContent = value;
    }
}

function showMessage(id, message, success = false) {
    const element = getElement(id);

    if (!element) {
        return;
    }

    element.textContent = message;
    element.style.color =
        success ? "#15803d" : "#dc2626";
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function readResponse(response) {

    const contentType =
        response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        return await response.json();
    }

    const text = await response.text();

    return text
        ? { message: text }
        : {};
}

/* =========================================================
   USER STATE
========================================================= */

function getStoredUser() {

    const user =
        localStorage.getItem(
            "resumeAnalyzerUser"
        );

    if (!user) {
        return null;
    }

    try {
        return JSON.parse(user);
    } catch (error) {
        return null;
    }
}

function saveUser(user) {

    currentUser = user;

    localStorage.setItem(
        "resumeAnalyzerUser",
        JSON.stringify(user)
    );
}

function clearUser() {

    currentUser = null;
    currentResumeId = null;
    currentResume = null;
    currentMatches = [];

    localStorage.removeItem(
        "resumeAnalyzerUser"
    );

    localStorage.removeItem(
        "currentResumeId"
    );
}

/* =========================================================
   PAGE STATE
========================================================= */

function showLoggedOutPage() {

    document.body.classList.remove(
        "logged-in"
    );

    document.body.classList.add(
        "logged-out"
    );

    const loginForm =
        getElement("loginForm");

    const registerForm =
        getElement("registerForm");

    if (loginForm) {
        loginForm.style.display = "flex";
    }

    if (registerForm) {
        registerForm.style.display = "none";
    }

    setText(
        "headerUsername",
        "User"
    );

    setText(
        "welcomeUsername",
        "User"
    );

    resetDashboard();
}

function showLoggedInPage(user) {

    document.body.classList.remove(
        "logged-out"
    );

    document.body.classList.add(
        "logged-in"
    );

    currentUser = user;

    const username =
        user.username ||
        user.userName ||
        user.name ||
        "User";

    setText(
        "headerUsername",
        username
    );

    setText(
        "welcomeUsername",
        username
    );

    const loginForm =
        getElement("loginForm");

    const registerForm =
        getElement("registerForm");

    if (loginForm) {
        loginForm.style.display = "none";
    }

    if (registerForm) {
        registerForm.style.display = "none";
    }

    loadInitialData();
}

/* =========================================================
   LOGIN / REGISTER SWITCH
========================================================= */

function showRegisterForm() {

    const loginForm =
        getElement("loginForm");

    const registerForm =
        getElement("registerForm");

    if (loginForm) {
        loginForm.style.display = "none";
    }

    if (registerForm) {
        registerForm.style.display = "flex";
    }

    showMessage(
        "loginMessage",
        ""
    );

    showMessage(
        "registerMessage",
        ""
    );
}

function showLoginForm() {

    const loginForm =
        getElement("loginForm");

    const registerForm =
        getElement("registerForm");

    if (loginForm) {
        loginForm.style.display = "flex";
    }

    if (registerForm) {
        registerForm.style.display = "none";
    }

    showMessage(
        "loginMessage",
        ""
    );

    showMessage(
        "registerMessage",
        ""
    );
}

/* =========================================================
   LOGIN
========================================================= */

async function loginUser() {

    const usernameElement =
        getElement("username");

    const passwordElement =
        getElement("password");

    const loginButton =
        getElement("loginButton");

    if (!usernameElement ||
        !passwordElement) {

        return;
    }

    const username =
        usernameElement.value.trim();

    const password =
        passwordElement.value;

    if (!username || !password) {

        showMessage(
            "loginMessage",
            "Please enter username and password."
        );

        return;
    }

    if (loginButton) {

        loginButton.disabled = true;
        loginButton.textContent =
            "Logging in...";
    }

    try {

        const response =
            await fetch(
                API_BASE_URL +
                LOGIN_URL,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    credentials: "include",

                    body: JSON.stringify({
                        username: username,
                        password: password
                    })
                }
            );

        const data =
            await readResponse(response);

        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                "Invalid username or password."
            );
        }

        const loggedInUser = {

            id:
                data.id ||
                data.userId ||
                null,

            username:
                data.username ||
                data.userName ||
                data.name ||
                username,

            email:
                data.email ||
                ""
        };

        saveUser(loggedInUser);

        showLoggedInPage(
            loggedInUser
        );

    } catch (error) {

        console.error(
            "Login error:",
            error
        );

        document.body.classList.remove(
            "logged-in"
        );

        document.body.classList.add(
            "logged-out"
        );

        showMessage(
            "loginMessage",
            error.message ||
            "Login failed."
        );

    } finally {

        if (loginButton) {

            loginButton.disabled = false;

            loginButton.textContent =
                "Login";
        }
    }
}

/* =========================================================
   REGISTER
========================================================= */

async function registerUser() {

    const username =
        getElement(
            "registerUsername"
        )?.value.trim();

    const email =
        getElement(
            "registerEmail"
        )?.value.trim();

    const password =
        getElement(
            "registerPassword"
        )?.value;

    const confirmPassword =
        getElement(
            "confirmPassword"
        )?.value;

    const registerButton =
        getElement(
            "registerButton"
        );

    if (!username ||
        !email ||
        !password ||
        !confirmPassword) {

        showMessage(
            "registerMessage",
            "Please fill in all fields."
        );

        return;
    }

    if (password !== confirmPassword) {

        showMessage(
            "registerMessage",
            "Passwords do not match."
        );

        return;
    }

    if (registerButton) {

        registerButton.disabled = true;

        registerButton.textContent =
            "Registering...";
    }

    try {

        const response =
            await fetch(
                API_BASE_URL +
                REGISTER_URL,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    credentials: "include",

                    body: JSON.stringify({
                        username: username,
                        email: email,
                        password: password
                    })
                }
            );

        const data =
            await readResponse(response);

        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                "Registration failed."
            );
        }

        showLoginForm();

        showMessage(
            "loginMessage",
            "Registration successful. Please login.",
            true
        );

    } catch (error) {

        console.error(
            "Registration error:",
            error
        );

        showMessage(
            "registerMessage",
            error.message ||
            "Registration failed."
        );

    } finally {

        if (registerButton) {

            registerButton.disabled = false;

            registerButton.textContent =
                "Register";
        }
    }
}

/* =========================================================
   LOGOUT
========================================================= */

async function logoutUser() {

    try {

        await fetch(
            API_BASE_URL +
            LOGOUT_URL,
            {
                method: "POST",
                credentials: "include"
            }
        );

    } catch (error) {

        console.warn(
            "Logout request failed:",
            error
        );
    }

    closeAllModals();

    clearUser();

    showLoggedOutPage();
}

/* =========================================================
   FILE SELECTION
========================================================= */

function setupFileSelection() {

    const fileInput =
        getElement("resumeFile");

    const selectedFileName =
        getElement(
            "selectedFileName"
        );

    if (!fileInput) {
        return;
    }

    fileInput.addEventListener(
        "change",
        function () {

            const file =
                fileInput.files[0];

            if (selectedFileName) {

                selectedFileName.textContent =
                    file
                        ? file.name
                        : "No file selected";
            }
        }
    );
}

/* =========================================================
   UPLOAD RESUME
========================================================= */

async function uploadResume() {

    const fileInput =
        getElement("resumeFile");

    const uploadButton =
        getElement("uploadButton");

    if (!fileInput ||
        !fileInput.files.length) {

        showMessage(
            "uploadMessage",
            "Please choose a PDF resume first."
        );

        return;
    }

    const file =
        fileInput.files[0];

    if (
        !file.name
            .toLowerCase()
            .endsWith(".pdf")
    ) {

        showMessage(
            "uploadMessage",
            "Please select a PDF file."
        );

        return;
    }

    const formData =
        new FormData();

    formData.append(
        "file",
        file
    );

    if (uploadButton) {

        uploadButton.disabled = true;

        uploadButton.textContent =
            "Uploading...";
    }

    showMessage(
        "uploadMessage",
        "Uploading resume..."
    );

    try {

        const response =
            await fetch(
                API_BASE_URL +
                UPLOAD_URL,
                {
                    method: "POST",

                    credentials: "include",

                    body: formData
                }
            );

        const data =
            await readResponse(response);

        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                "Resume upload failed."
            );
        }

        currentResume = data;

        currentResumeId =
            data.id;

        if (currentResumeId) {

            localStorage.setItem(
                "currentResumeId",
                currentResumeId
            );
        }

        // Create ATS analysis automatically
        if (currentResumeId) {

            const analysisResponse =
                await fetch(
                    API_BASE_URL +
                    "/api/analysis/" +
                    currentResumeId,
                    {
                        method: "POST",
                        credentials: "include"
                    }
                );

            const analysisData =
                await readResponse(
                    analysisResponse
                );

            if (!analysisResponse.ok) {

                console.warn(
                    "ATS analysis could not be created:",
                    analysisData.message ||
                    analysisData.error
                );
            } else {

                console.log(
                    "ATS analysis created successfully:",
                    analysisData
                );
            }
        }

        showMessage(
            "uploadMessage",
            "Resume uploaded successfully.",
            true
        );

        await loadResumeHistory();

        if (currentResumeId) {

            await analyzeAllJobs(
                currentResumeId,
                true
            );
        }

    } catch (error) {

        console.error(
            "Upload error:",
            error
        );

        showMessage(
            "uploadMessage",
            error.message ||
            "Resume upload failed."
        );

    } finally {

        if (uploadButton) {

            uploadButton.disabled = false;

            uploadButton.textContent =
                "Upload & Analyze";
        }
    }
}

/* =========================================================
   INITIAL DATA
========================================================= */

async function loadInitialData() {

    resetDashboard();

    await loadResumeHistory();

    const savedResumeId =
        localStorage.getItem(
            "currentResumeId"
        );

    if (savedResumeId) {

        currentResumeId =
            Number(savedResumeId);

        await analyzeAllJobs(
            currentResumeId,
            false
        );
    }
}

/* =========================================================
   RESUME HISTORY
========================================================= */

async function loadResumeHistory() {

    const container =
        getElement(
            "resumeHistoryContainer"
        );

    if (!container) {
        return;
    }

    container.innerHTML = `
        <div class="empty-state">
            <p>Loading resume history...</p>
        </div>
    `;

    try {

        const response =
            await fetch(
                API_BASE_URL +
                RESUMES_URL,
                {
                    method: "GET",
                    credentials: "include"
                }
            );

        const data =
            await readResponse(response);

        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                "Unable to load resume history."
            );
        }

        const resumes =
            Array.isArray(data)
                ? data
                : data.content || [];

        if (!resumes.length) {

            container.innerHTML = `
                <div class="empty-state">
                    <h3>No resume history yet</h3>
                    <p>Upload a resume to see it here.</p>
                </div>
            `;

            return;
        }

        container.innerHTML =
            resumes.map(
                resume => {

                    const fileName =
                        escapeHtml(
                            resume.fileName ||
                            "Resume"
                        );

                    return `
                        <div class="resume-history-card">

                            <h3>
                                ${fileName}
                            </h3>

                            <p>
                                Resume ID:
                                ${resume.id}
                            </p>

                            <div class="resume-history-buttons">

                                <button
                                    type="button"
                                    class="history-button analyze-button"
                                    onclick="analyzeSelectedResume(${resume.id})">
                                    📊 Normal Analyze
                                </button>

                                <button
                                    type="button"
                                    class="history-button ai-button"
                                    onclick="analyzeResumeWithAI(${resume.id})">
                                    🤖 AI Analyze
                                </button>

                                <button
                                   type="button"
                                   class="history-button view-button"
                                   onclick="viewResume(${resume.id})">
                                   View
                                </button>

                                <button
                                   type="button"
                                   class="history-button download-button"
                                   onclick="downloadResume(${resume.id})">
                                   ⬇️ Download
                                </button>

                                <button
                                    type="button"
                                    class="history-button delete-button"
                                    onclick="deleteResume(${resume.id})">
                                    Delete
                                </button>

                            </div>

                        </div>
                    `;
                }
            ).join("");

    } catch (error) {

        console.error(
            "Resume history error:",
            error
        );

        container.innerHTML = `
            <div class="empty-state">
                <h3>
                    Unable to load resume history
                </h3>

                <p>
                    ${escapeHtml(
            error.message
        )}
                </p>
            </div>
        `;
    }
}

/* =========================================================
   DOWNLOAD RESUME
========================================================= */

async function downloadResume(resumeId) {

    if (!resumeId) {

        alert("Invalid resume ID.");

        return;
    }

    try {

        const response =
            await fetch(
                API_BASE_URL +
                "/api/resumes/" +
                resumeId +
                "/download",
                {
                    method: "GET",
                    credentials: "include"
                }
            );

        if (!response.ok) {

            const data =
                await readResponse(response);

            throw new Error(
                data.message ||
                data.error ||
                "Unable to download resume."
            );
        }

        const blob =
            await response.blob();

        const contentDisposition =
            response.headers.get(
                "Content-Disposition"
            );

        let filename =
            "Resume.pdf";

        if (contentDisposition) {

            const match =
                contentDisposition.match(
                    /filename="?([^"]+)"?/i
                );

            if (match && match[1]) {

                filename =
                    match[1];
            }
        }

        const url =
            URL.createObjectURL(blob);

        const link =
            document.createElement("a");

        link.href = url;

        link.download = filename;

        document.body.appendChild(link);

        link.click();

        link.remove();

        setTimeout(
            () => {
                URL.revokeObjectURL(url);
            },
            1000
        );

    } catch (error) {

        console.error(
            "Download resume error:",
            error
        );

        alert(
            error.message ||
            "Unable to download resume."
        );
    }
}

/* =========================================================
   NORMAL ANALYZE
   IMPORTANT:
   DOES NOT CALL /api/resumes/{id}
========================================================= */

async function analyzeSelectedResume(
    resumeId
) {

    if (!resumeId) {

        alert(
            "Invalid resume ID."
        );

        return;
    }

    currentResumeId =
        Number(resumeId);

    localStorage.setItem(
        "currentResumeId",
        currentResumeId
    );

    const buttons =
        document.querySelectorAll(
            ".analyze-button"
        );

    buttons.forEach(
        button => {
            button.disabled = true;
        }
    );

    try {

        // Get the real ATS analysis
        let response =
            await fetch(
                API_BASE_URL +
                "/api/analysis/" +
                currentResumeId,
                {
                    method: "GET",
                    credentials: "include"
                }
            );

        let analysis =
            await readResponse(response);

        // If analysis doesn't exist, create it
        if (response.status === 404) {

            response =
                await fetch(
                    API_BASE_URL +
                    "/api/analysis/" +
                    currentResumeId,
                    {
                        method: "POST",
                        credentials: "include"
                    }
                );

            analysis =
                await readResponse(response);
        }

        if (!response.ok) {

            throw new Error(
                analysis.message ||
                analysis.error ||
                "Unable to analyze resume."
            );
        }

        // Keep job matching working
        try {

            currentMatches =
                await loadJobRanking(
                    currentResumeId
                );

            currentMatches =
                await addJobTitles(
                    currentMatches
                );

            updateDashboard(
                currentMatches
            );

            displayRecommendedJobs(
                currentMatches
            );

            displayImprovements(
                currentMatches
            );

        } catch (jobError) {

            console.warn(
                "Job matching data could not be loaded:",
                jobError
            );

            currentMatches = [];
        }

        const modal =
            getElement(
                "analysisModal"
            );

        if (!modal) {

            throw new Error(
                "Analysis modal was not found."
            );
        }

        setText(
            "analysisResumeName",
            currentResume?.fileName ||
            "Resume Analysis"
        );

        // Real ATS scores
        const overallScore =
            Number(
                analysis.overallScore || 0
            );

        const skillsScore =
            Number(
                analysis.skillsScore || 0
            );

        const experienceScore =
            Number(
                analysis.experienceScore || 0
            );

        const educationScore =
            Number(
                analysis.educationScore || 0
            );

        const formatScore =
            Number(
                analysis.formatScore || 0
            );

        const keywordScore =
            Number(
                analysis.keywordScore || 0
            );

        // Overall score
        setText(
            "analysisOverallScore",
            overallScore
        );

        setText(
            "analysisScoreLabel",
            overallScore >= 80
                ? "Excellent ATS Score"
                : overallScore >= 60
                    ? "Good ATS Score"
                    : "Resume Needs Improvement"
        );

        setText(
            "analysisSummary",
            "Your resume has been analyzed using the ATS resume analysis."
        );

        // Category scores
        setText(
            "analysisSkillsScore",
            skillsScore + "/100"
        );

        setText(
            "analysisExperienceScore",
            experienceScore + "/100"
        );

        setText(
            "analysisEducationScore",
            educationScore + "/100"
        );

        setText(
            "analysisFormatScore",
            formatScore + "/100"
        );

        setText(
            "analysisKeywordScore",
            keywordScore + "/100"
        );

        // Progress bars
        const progressValues = {

            analysisSkillsProgress:
            skillsScore,

            analysisExperienceProgress:
            experienceScore,

            analysisEducationProgress:
            educationScore,

            analysisFormatProgress:
            formatScore,

            analysisKeywordProgress:
            keywordScore
        };

        Object.entries(
            progressValues
        ).forEach(
            ([id, score]) => {

                const element =
                    getElement(id);

                if (element) {

                    element.style.width =
                        Math.max(
                            0,
                            Math.min(
                                100,
                                score
                            )
                        ) + "%";
                }
            }
        );

        // Real strengths
        setText(
            "analysisStrengths",
            analysis.strengths ||
            "No specific strengths were identified."
        );

        // Real weaknesses
        setText(
            "analysisWeaknesses",
            analysis.weaknesses ||
            "No specific weaknesses were identified."
        );

        // Real recommendations
        setText(
            "analysisRecommendations",
            analysis.recommendations ||
            "No recommendations are available."
        );

        // Open modal
        modal.classList.add(
            "is-open"
        );

        modal.style.display =
            "flex";

        modal.removeAttribute(
            "hidden"
        );

    } catch (error) {

        console.error(
            "Normal analyze error:",
            error
        );

        alert(
            error.message ||
            "Unable to analyze selected resume."
        );

    } finally {

        buttons.forEach(
            button => {
                button.disabled = false;
            }
        );
    }
}

/* =========================================================
   VIEW RESUME
========================================================= */

async function viewResume(
    resumeId
) {

    if (!resumeId) {

        alert(
            "Invalid resume ID."
        );

        return;
    }

    try {

        const response =
            await fetch(
                API_BASE_URL +
                "/api/resumes/" +
                resumeId,
                {
                    method: "GET",
                    credentials: "include"
                }
            );

        const data =
            await readResponse(
                response
            );

        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                "Unable to view resume."
            );
        }

        const modal =
            getElement(
                "resumeModal"
            );

        if (!modal) {
            return;
        }

        setText(
            "modalResumeFileName",
            data.fileName ||
            "Resume Details"
        );

        setText(
            "modalFileName",
            data.fileName ||
            "-"
        );

        setText(
            "modalUploadedDate",
            data.uploadedAt
                ? new Date(
                    data.uploadedAt
                ).toLocaleString()
                : "-"
        );

        const textContainer =
            getElement(
                "modalResumeText"
            );

        if (textContainer) {

            textContainer.textContent =
                data.resumeText ||
                "No resume text available.";
        }

        modal.classList.add(
            "is-open"
        );

        modal.style.display =
            "flex";

        modal.removeAttribute(
            "hidden"
        );

    } catch (error) {

        console.error(
            "View resume error:",
            error
        );

        alert(
            error.message ||
            "Unable to view resume."
        );
    }
}

/* =========================================================
   DELETE RESUME
========================================================= */

async function deleteResume(
    resumeId
) {

    if (!resumeId) {

        alert(
            "Invalid resume ID."
        );

        return;
    }

    if (!confirm(
        "Are you sure you want to delete this resume?"
    )) {

        return;
    }

    try {

        const response =
            await fetch(
                API_BASE_URL +
                "/api/resumes/" +
                resumeId,
                {
                    method: "DELETE",
                    credentials: "include"
                }
            );

        const data =
            await readResponse(
                response
            );

        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                "Unable to delete resume."
            );
        }

        if (
            Number(currentResumeId) ===
            Number(resumeId)
        ) {

            currentResumeId = null;
            currentResume = null;
            currentMatches = [];

            localStorage.removeItem(
                "currentResumeId"
            );

            resetDashboard();
        }

        await loadResumeHistory();

        alert(
            data.message ||
            "Resume deleted successfully."
        );

    } catch (error) {

        console.error(
            "Delete resume error:",
            error
        );

        alert(
            error.message ||
            "Unable to delete resume."
        );
    }
}

/* =========================================================
   LOAD JOBS
========================================================= */

async function loadJobs() {

    const response =
        await fetch(
            API_BASE_URL +
            JOBS_URL,
            {
                method: "GET",
                credentials: "include"
            }
        );

    const data =
        await readResponse(
            response
        );

    if (!response.ok) {

        throw new Error(
            data.message ||
            data.error ||
            "Unable to load job descriptions."
        );
    }

    return Array.isArray(data)
        ? data
        : data.content || [];
}

/* =========================================================
   LOAD JOB RANKING
========================================================= */

async function loadJobRanking(
    resumeId
) {

    const response =
        await fetch(
            API_BASE_URL +
            JOB_MATCH_URL +
            "/ranking/" +
            resumeId,
            {
                method: "GET",
                credentials: "include"
            }
        );

    const data =
        await readResponse(
            response
        );

    if (!response.ok) {

        throw new Error(
            data.message ||
            data.error ||
            "Unable to load job ranking."
        );
    }

    return Array.isArray(data)
        ? data
        : data.content || [];
}

/* =========================================================
   ANALYZE ALL JOBS
========================================================= */

async function analyzeAllJobs(
    resumeId,
    createMissingMatches = false
) {

    if (!resumeId) {

        resetDashboard();

        return;
    }

    try {

        let ranking = [];

        try {

            ranking =
                await loadJobRanking(
                    resumeId
                );

        } catch (rankingError) {

            console.warn(
                "Existing ranking unavailable:",
                rankingError
            );
        }

        /*
         * Existing matches found.
         * Use them.
         * Do NOT create duplicates.
         */

        if (ranking.length) {

            currentMatches =
                await addJobTitles(
                    ranking
                );

            updateDashboard(
                currentMatches
            );

            displayRecommendedJobs(
                currentMatches
            );

            displayImprovements(
                currentMatches
            );

            return;
        }

        /*
         * Normal Analyze should not
         * create duplicate matches.
         */

        if (!createMissingMatches) {

            resetDashboard();

            return;
        }

        const jobs =
            await loadJobs();

        if (!jobs.length) {

            resetDashboard();

            return;
        }

        currentMatches = [];

        for (const job of jobs) {

            try {

                const response =
                    await fetch(
                        API_BASE_URL +
                        JOB_MATCH_URL +
                        "/" +
                        resumeId +
                        "/" +
                        job.id,
                        {
                            method: "POST",
                            credentials: "include"
                        }
                    );

                const data =
                    await readResponse(
                        response
                    );

                if (response.ok) {

                    currentMatches.push({
                        ...data,

                        jobTitle:
                            data.jobTitle ||
                            job.title ||
                            "Job Description"
                    });

                } else {

                    console.warn(
                        "Job " +
                        job.id +
                        " was not analyzed:",
                        data.message ||
                        data.error
                    );
                }

            } catch (error) {

                console.error(
                    "Error analyzing job " +
                    job.id,
                    error
                );
            }
        }

        updateDashboard(
            currentMatches
        );

        displayRecommendedJobs(
            currentMatches
        );

        displayImprovements(
            currentMatches
        );

    } catch (error) {

        console.error(
            "Analysis error:",
            error
        );

        resetDashboard();
    }
}

/* =========================================================
   ADD JOB TITLES
========================================================= */

async function addJobTitles(
    ranking
) {

    try {

        const jobs =
            await loadJobs();

        const jobMap =
            new Map();

        jobs.forEach(
            job => {

                jobMap.set(
                    Number(job.id),
                    job.title
                );
            }
        );

        return ranking.map(
            match => ({

                ...match,

                jobTitle:
                    match.jobTitle ||
                    match.title ||
                    jobMap.get(
                        Number(
                            match.jobDescriptionId
                        )
                    ) ||
                    "Job Description"
            })
        );

    } catch (error) {

        console.warn(
            "Could not add job titles:",
            error
        );

        return ranking.map(
            match => ({

                ...match,

                jobTitle:
                    match.jobTitle ||
                    match.title ||
                    "Job Description"
            })
        );
    }
}

/* =========================================================
   RESET DASHBOARD
========================================================= */

function resetDashboard() {

    setText(
        "totalJobs",
        "0"
    );

    setText(
        "bestScore",
        "0%"
    );

    setText(
        "averageScore",
        "0%"
    );

    setText(
        "totalResumes",
        "0"
    );

    const missingContainer =
        getElement(
            "commonMissingSkills"
        );

    if (missingContainer) {

        missingContainer.innerHTML = `
            <p>
                Upload a resume to see missing skills.
            </p>
        `;
    }

    const jobsContainer =
        getElement(
            "jobsContainer"
        );

    if (jobsContainer) {

        jobsContainer.innerHTML = `
            <div class="empty-state">
                <p>
                    Analyze a resume to see recommended jobs.
                </p>
            </div>
        `;
    }

    const improvementsContainer =
        getElement(
            "improvementsContainer"
        );

    if (improvementsContainer) {

        improvementsContainer.innerHTML = `
            <div class="empty-state">
                <p>
                    Analyze a resume to see improvement suggestions.
                </p>
            </div>
        `;
    }
}

/* =========================================================
   UPDATE DASHBOARD
========================================================= */

function updateDashboard(
    matches
) {

    if (
        !Array.isArray(matches) ||
        !matches.length
    ) {

        resetDashboard();

        return;
    }

    const scores =
        matches
            .map(
                match =>
                    Number(
                        match.matchScore ||
                        match.score ||
                        0
                    )
            )
            .filter(
                score =>
                    !Number.isNaN(score)
            );

    const bestScore =
        scores.length
            ? Math.max(...scores)
            : 0;

    const averageScore =
        scores.length
            ? Math.round(
                scores.reduce(
                    (sum, score) =>
                        sum + score,
                    0
                ) / scores.length
            )
            : 0;

    setText(
        "totalJobs",
        matches.length
    );

    setText(
        "bestScore",
        bestScore + "%"
    );

    setText(
        "averageScore",
        averageScore + "%"
    );

    loadResumeCount();

    updateCommonMissingSkills(
        matches
    );
}

/* =========================================================
   RESUME COUNT
========================================================= */

async function loadResumeCount() {

    try {

        const response =
            await fetch(
                API_BASE_URL +
                RESUMES_URL,
                {
                    method: "GET",
                    credentials: "include"
                }
            );

        if (!response.ok) {
            return;
        }

        const data =
            await response.json();

        const resumes =
            Array.isArray(data)
                ? data
                : data.content || [];

        setText(
            "totalResumes",
            resumes.length
        );

    } catch (error) {

        console.error(
            "Failed to load resume count:",
            error
        );
    }
}

/* =========================================================
   COMMON MISSING SKILLS
========================================================= */

function updateCommonMissingSkills(
    matches
) {

    const container =
        getElement(
            "commonMissingSkills"
        );

    if (!container) {
        return;
    }

    const skillCounts = {};

    matches.forEach(
        match => {

            let missing = "";

            if (
                Array.isArray(
                    match.missingSkills
                )
            ) {

                missing =
                    match.missingSkills.join(
                        ","
                    );

            } else {

                missing =
                    String(
                        match.missingSkills ||
                        ""
                    );
            }

            missing
                .split(",")
                .map(
                    skill =>
                        skill.trim()
                            .toLowerCase()
                )
                .filter(Boolean)
                .forEach(
                    skill => {

                        skillCounts[skill] =
                            (
                                skillCounts[skill] ||
                                0
                            ) + 1;
                    }
                );
        }
    );

    const skills =
        Object.entries(
            skillCounts
        )
            .sort(
                (a, b) =>
                    b[1] - a[1]
            )
            .map(
                entry =>
                    entry[0]
            );

    if (!skills.length) {

        container.innerHTML =
            "<p>No major missing skills found.</p>";

        return;
    }

    container.innerHTML =
        skills.map(
            skill => `
                <span class="skill-tag">
                    ${escapeHtml(skill)}
                </span>
            `
        ).join("");
}

/* =========================================================
   RECOMMENDED JOBS
========================================================= */

function displayRecommendedJobs(
    matches
) {

    const container =
        getElement(
            "jobsContainer"
        );

    if (!container) {
        return;
    }

    if (
        !Array.isArray(matches) ||
        !matches.length
    ) {

        container.innerHTML = `
            <div class="empty-state">
                <h3>No job matches yet</h3>
                <p>
                    Analyze your resume to see recommended jobs.
                </p>
            </div>
        `;

        return;
    }

    const uniqueJobs =
        new Map();

    matches.forEach(
        match => {

            const title =
                match.jobTitle ||
                "Job Description";

            const score =
                Number(
                    match.matchScore ||
                    match.score ||
                    0
                );

            const existing =
                uniqueJobs.get(
                    title
                );

            if (
                !existing ||
                score >
                Number(
                    existing.matchScore ||
                    existing.score ||
                    0
                )
            ) {

                uniqueJobs.set(
                    title,
                    match
                );
            }
        }
    );

    const jobs =
        Array.from(
            uniqueJobs.values()
        )
            .sort(
                (a, b) =>
                    Number(
                        b.matchScore ||
                        b.score ||
                        0
                    ) -
                    Number(
                        a.matchScore ||
                        a.score ||
                        0
                    )
            );

    container.innerHTML =
        jobs.map(
            match => {

                const score =
                    Number(
                        match.matchScore ||
                        match.score ||
                        0
                    );

                return `
                    <div class="job-card">

                        <h3>
                            ${escapeHtml(
                    match.jobTitle ||
                    "Job Description"
                )}
                        </h3>

                        <div class="job-score">
                            Match Score
                            <strong>
                                ${score}%
                            </strong>
                        </div>

                        <p>
                            <strong>
                                Matching Skills:
                            </strong>

                            ${escapeHtml(
                    Array.isArray(
                        match.matchingSkills
                    )
                        ? match.matchingSkills.join(
                            ", "
                        )
                        : match.matchingSkills ||
                        "None"
                )}
                        </p>

                        <p>
                            <strong>
                                Missing Skills:
                            </strong>

                            ${escapeHtml(
                    Array.isArray(
                        match.missingSkills
                    )
                        ? match.missingSkills.join(
                            ", "
                        )
                        : match.missingSkills ||
                        "None"
                )}
                        </p>

                        <p class="job-recommendation">
                            ${escapeHtml(
                    match.recommendation ||
                    "No recommendation available."
                )}
                        </p>

                    </div>
                `;
            }
        ).join("");
}

/* =========================================================
   IMPROVEMENTS
========================================================= */

function displayImprovements(
    matches
) {

    const container =
        getElement(
            "improvementsContainer"
        );

    if (!container) {
        return;
    }

    const skills = [];

    matches.forEach(
        match => {

            const missing =
                Array.isArray(
                    match.missingSkills
                )
                    ? match.missingSkills
                    : String(
                        match.missingSkills ||
                        ""
                    ).split(",");

            missing
                .map(
                    skill =>
                        skill.trim()
                )
                .filter(Boolean)
                .forEach(
                    skill => {

                        if (
                            !skills.some(
                                existing =>
                                    existing.toLowerCase() ===
                                    skill.toLowerCase()
                            )
                        ) {

                            skills.push(
                                skill
                            );
                        }
                    }
                );
        }
    );

    if (!skills.length) {

        container.innerHTML = `
            <div class="improvement-card">

                <h3>
                    Your resume is well matched
                </h3>

                <p>
                    No major missing skills were identified
                    from the available jobs.
                </p>

            </div>
        `;

        return;
    }

    container.innerHTML = `
        <div class="improvement-card">

            <h3>
                Recommended Skills to Improve
            </h3>

            <ul>

                ${skills.map(
        skill => `
                        <li>
                            ${escapeHtml(skill)}
                        </li>
                    `
    ).join("")}

            </ul>

        </div>
    `;
}

/* =========================================================
   AI ANALYSIS
========================================================= */

async function analyzeResumeWithAI(
    resumeId
) {

    if (!resumeId) {

        alert(
            "Invalid resume ID."
        );

        return;
    }

    currentResumeId =
        Number(resumeId);

    localStorage.setItem(
        "currentResumeId",
        currentResumeId
    );

    const modal =
        getElement(
            "aiAnalysisModal"
        );

    const content =
        getElement(
            "aiAnalysisText"
        );

    if (modal) {

        modal.hidden = false;

        modal.classList.remove(
            "hidden"
        );

        modal.classList.add(
            "is-open"
        );

        modal.style.display =
            "flex";

        modal.setAttribute(
            "aria-hidden",
            "false"
        );
    }

    if (content) {

        content.innerHTML = `
            <div class="ai-loading">

                <h3>
                    🤖 AI is analyzing your resume...
                </h3>

                <p>
                    Please wait.
                </p>

            </div>
        `;
    }

    try {

        const response =
            await fetch(
                API_BASE_URL +
                "/api/ai/analyze/" +
                resumeId,
                {
                    method: "POST",
                    credentials: "include"
                }
            );

        const data =
            await readResponse(
                response
            );

        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                "AI analysis failed."
            );
        }

        const aiResponse =
            data.aiAnalysis ||
            data.analysis ||
            data.message ||
            "No AI analysis received.";

        if (content) {

            content.innerHTML =
                formatAIAnalysis(
                    aiResponse
                );
        }

    } catch (error) {

        console.error(
            "AI analysis error:",
            error
        );

        if (content) {

            content.innerHTML = `
                <div class="ai-error">

                    <h3>
                        AI Analysis Failed
                    </h3>

                    <p>
                        ${escapeHtml(
                error.message ||
                "Unable to analyze resume."
            )}
                    </p>

                </div>
            `;
        }
    }
}

/* =========================================================
   FORMAT AI ANALYSIS
========================================================= */

function formatAIAnalysis(
    text
) {

    if (!text) {

        return `
            <p>
                No AI analysis available.
            </p>
        `;
    }

    let html =
        escapeHtml(text);

    html =
        html.replace(
            /^###\s*(.+)$/gm,
            "<h3>$1</h3>"
        );

    html =
        html.replace(
            /^##\s*(.+)$/gm,
            "<h2>$1</h2>"
        );

    html =
        html.replace(
            /^#\s*(.+)$/gm,
            "<h2>$1</h2>"
        );

    html =
        html.replace(
            /\*\*(.*?)\*\*/g,
            "<strong>$1</strong>"
        );

    html =
        html.replace(
            /^[\-\*]\s+(.+)$/gm,
            "<li>$1</li>"
        );

    html =
        html.replace(
            /^\d+\.\s+(.+)$/gm,
            "<li>$1</li>"
        );

    html =
        html.replace(
            /(<li>.*?<\/li>)/gs,
            "<ul>$1</ul>"
        );

    html =
        html.replace(
            /\n{2,}/g,
            "</p><p>"
        );

    html =
        "<p>" +
        html +
        "</p>";

    return `
        <div class="ai-analysis-content">
            ${html}
        </div>
    `;
}

/* =========================================================
   CLOSE NORMAL ANALYSIS MODAL
========================================================= */

function closeAnalysisModal() {

    const modal =
        getElement(
            "analysisModal"
        );

    if (!modal) {
        return;
    }

    modal.classList.remove(
        "is-open"
    );

    modal.style.display =
        "none";

    modal.setAttribute(
        "hidden",
        ""
    );
}

/* =========================================================
   CLOSE RESUME MODAL
========================================================= */

function closeResumeModal() {

    const modal =
        getElement(
            "resumeModal"
        );

    if (!modal) {
        return;
    }

    modal.classList.remove(
        "is-open"
    );

    modal.style.display =
        "none";

    modal.setAttribute(
        "hidden",
        ""
    );
}

/* =========================================================
   CLOSE AI MODAL
========================================================= */

function closeAIAnalysisModal() {

    const modal =
        getElement(
            "aiAnalysisModal"
        );

    if (!modal) {
        return;
    }

    modal.classList.remove(
        "is-open"
    );

    modal.classList.add(
        "hidden"
    );

    modal.hidden = true;

    modal.style.display =
        "none";

    modal.setAttribute(
        "aria-hidden",
        "true"
    );
}

/* =========================================================
   CLOSE ALL MODALS
========================================================= */

function closeAllModals() {

    closeAnalysisModal();

    closeResumeModal();

    closeAIAnalysisModal();
}

/* =========================================================
   DOWNLOAD NORMAL ANALYSIS
========================================================= */

function downloadAnalysisReport() {

    const modal =
        getElement(
            "analysisModal"
        );

    if (!modal) {

        alert(
            "Analysis report is not available."
        );

        return;
    }

    const text =
        modal.innerText ||
        modal.textContent ||
        "";

    if (!text.trim()) {

        alert(
            "Analysis report is empty."
        );

        return;
    }

    downloadTextFile(
        text,
        "Resume_Analysis_Report.txt"
    );
}

/* =========================================================
   PRINT AI ANALYSIS
========================================================= */

function printAIAnalysis() {

    const content =
        getElement(
            "aiAnalysisText"
        );

    if (!content) {

        alert(
            "No AI analysis available."
        );

        return;
    }

    const printWindow =
        window.open(
            "",
            "_blank"
        );

    if (!printWindow) {

        alert(
            "Please allow pop-ups to print the AI analysis."
        );

        return;
    }

    printWindow.document.write(`
        <!DOCTYPE html>

        <html>

        <head>

            <title>
                AI Resume Analysis
            </title>

            <style>

                body {
                    font-family: Arial, sans-serif;
                    padding: 40px;
                    line-height: 1.7;
                    color: #172033;
                }

                h1 {
                    color: #2563eb;
                }

                h2,
                h3 {
                    color: #172033;
                    margin-top: 25px;
                }

                li {
                    margin-bottom: 8px;
                }

            </style>

        </head>

        <body>

            <h1>
                AI Resume Analysis
            </h1>

            ${content.innerHTML}

        </body>

        </html>
    `);

    printWindow.document.close();

    printWindow.focus();

    setTimeout(
        () => {
            printWindow.print();
        },
        300
    );
}

/* =========================================================
   DOWNLOAD AI ANALYSIS
========================================================= */

function downloadAIAnalysis() {

    const content =
        getElement(
            "aiAnalysisText"
        );

    if (!content) {

        alert(
            "No AI analysis available."
        );

        return;
    }

    const text =
        content.innerText ||
        content.textContent ||
        "";

    if (!text.trim()) {

        alert(
            "No AI analysis available."
        );

        return;
    }

    downloadTextFile(
        text,
        "AI_Resume_Analysis.txt"
    );
}

/* =========================================================
   DOWNLOAD TEXT FILE
========================================================= */

function downloadTextFile(
    text,
    filename
) {

    const blob =
        new Blob(
            [text],
            {
                type:
                    "text/plain;charset=utf-8"
            }
        );

    const url =
        URL.createObjectURL(
            blob
        );

    const link =
        document.createElement(
            "a"
        );

    link.href = url;

    link.download =
        filename;

    document.body.appendChild(
        link
    );

    link.click();

    link.remove();

    setTimeout(
        () => {

            URL.revokeObjectURL(
                url
            );

        },
        1000
    );
}

/* =========================================================
   MODAL BACKDROP
========================================================= */

document.addEventListener(
    "click",
    function (event) {

        const analysisModal =
            getElement(
                "analysisModal"
            );

        const resumeModal =
            getElement(
                "resumeModal"
            );

        const aiModal =
            getElement(
                "aiAnalysisModal"
            );

        if (
            event.target ===
            analysisModal
        ) {

            closeAnalysisModal();
        }

        if (
            event.target ===
            resumeModal
        ) {

            closeResumeModal();
        }

        if (
            event.target ===
            aiModal
        ) {

            closeAIAnalysisModal();
        }
    }
);

/* =========================================================
   ESC KEY
========================================================= */

document.addEventListener(
    "keydown",
    function (event) {

        if (
            event.key === "Escape"
        ) {

            closeAllModals();
        }
    }
);

/* =========================================================
   START APPLICATION
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        setupFileSelection();

        // Clear frontend login when browser is refreshed
        localStorage.removeItem(
            "resumeAnalyzerUser"
        );

        localStorage.removeItem(
            "currentResumeId"
        );

        currentUser = null;
        currentResumeId = null;
        currentResume = null;
        currentMatches = [];

        // Always show Login page after refresh
        showLoggedOutPage();
    }
);