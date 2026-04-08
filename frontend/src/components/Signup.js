import { useState } from "react";
import { signup } from "../api/auth";
import { TextField, Button, Box } from "@mui/material";

export default function Signup() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        await signup({ email, password });
        alert("Регистрация успешна");
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

            <Button fullWidth variant="outlined" type="submit" sx={{ mt: 2 }}>
                Создать аккаунт
            </Button>
        </Box>
    );
}
