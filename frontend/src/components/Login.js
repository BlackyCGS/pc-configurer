import { useState } from "react";
import { login } from "../api/auth";
import { TextField, Button, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await login({ email, password });

            navigate("/home");
        } catch {
            alert("Ошибка авторизации");
        }
    };

    return (
        <Box component="form" onSubmit={handleSubmit}>
            <TextField
                fullWidth
                label="Email"
                margin="normal"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />

            <TextField
                fullWidth
                label="Пароль"
                type="password"
                margin="normal"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />

            <Button fullWidth variant="contained" type="submit" sx={{ mt: 2 }}>
                Войти
            </Button>
        </Box>
    );
}
