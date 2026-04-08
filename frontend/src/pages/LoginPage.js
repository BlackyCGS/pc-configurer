import { useState } from "react";
import { Box, Paper, Tabs, Tab, Typography } from "@mui/material";
import Login from "../components/Login";
import Signup from "../components/Signup";

export default function LoginPage({ onLogin }) {
    const [tab, setTab] = useState(0);

    return (
        <Box
            sx={{
                minHeight: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                p: 2,
                background: (theme) =>
                    theme.palette.mode === "light"
                        ? "linear-gradient(135deg, #e3f2fd, #bbdefb)"
                        : "linear-gradient(135deg, #0d1117, #161b22)",
            }}
        >
            <Paper
                elevation={6}
                sx={{
                    width: "100%",
                    maxWidth: 420,
                    p: 4,
                    borderRadius: 4,
                    backdropFilter: "blur(10px)",
                }}
            >
                <Typography variant="h4" textAlign="center" mb={3}>
                    PC Configurer
                </Typography>

                <Tabs
                    value={tab}
                    onChange={(e, v) => setTab(v)}
                    variant="fullWidth"
                    sx={{ mb: 3 }}
                >
                    <Tab label="Вход" />
                    <Tab label="Регистрация" />
                </Tabs>

                {tab === 0 && <Login onLogin={onLogin} />}
                {tab === 1 && <Signup />}
            </Paper>
        </Box>
    );
}
