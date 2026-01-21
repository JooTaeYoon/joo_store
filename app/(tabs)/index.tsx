import { FontAwesome6 } from '@expo/vector-icons';
import { useRouter } from 'expo-router'; // 화면 이동을 위한 훅
import React, { useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';

export default function CustomerCreateScreen() {
  const [name, setName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  // 전화번호 하이픈 자동 입력 로직 (패턴 유지)
  const formatPhoneNumber = (text: string) => {
    const cleaned = text.replace(/\D/g, '');
    const match = cleaned.match(/^(\d{3})(\d{3,4})(\d{4})$/);
    if (match) return `${match[1]}-${match[2]}-${match[3]}`;
    return cleaned;
  };

  const handleRegister = async () => {
    if (!name || !phoneNumber) {
      Alert.alert('알림', '이름과 전화번호를 모두 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch('http://10.0.2.2:8080/api/store/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, phoneNumber }),
      });

      if (response.ok) {
        Alert.alert('성공', `${name} 손님이 등록되었습니다.`, [
          { text: '확인', onPress: () => router.push('/explore') }, // 등록 후 목록으로 이동
        ]);
        setName('');
        setPhoneNumber('');
      } else {
        throw new Error('등록 실패');
      }
    } catch (error) {
      Alert.alert('오류', '서버 통신 실패. IP 설정을 확인하세요.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <FontAwesome6 name="user-plus" size={40} color="#2e7d32" />
        <Text style={styles.title}>신규 손님 등록</Text>
      </View>

      <View style={styles.inputContainer}>
        <View style={styles.inputWrapper}>
          <FontAwesome6
            name="user"
            size={18}
            color="#666"
            style={styles.icon}
          />
          <TextInput
            style={styles.input}
            placeholder="성함"
            value={name}
            onChangeText={setName}
          />
        </View>

        <View style={styles.inputWrapper}>
          <FontAwesome6
            name="phone"
            size={18}
            color="#666"
            style={styles.icon}
          />
          <TextInput
            style={styles.input}
            placeholder="전화번호"
            value={phoneNumber}
            keyboardType="phone-pad"
            maxLength={13}
            onChangeText={(text) => setPhoneNumber(formatPhoneNumber(text))}
          />
        </View>

        {/* 등록 버튼 */}
        <TouchableOpacity
          style={styles.button}
          onPress={handleRegister}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.buttonText}>등록하기</Text>
          )}
        </TouchableOpacity>

        {/* 모든 손님 보기 버튼 (추가됨) */}
        <TouchableOpacity
          style={styles.outlineButton}
          onPress={() => router.push('/explore')}
        >
          <FontAwesome6
            name="users"
            size={16}
            color="#2e7d32"
            style={{ marginRight: 8 }}
          />
          <Text style={styles.outlineButtonText}>모든 손님 보기</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    padding: 20,
    justifyContent: 'center',
  },
  header: { alignItems: 'center', marginBottom: 30 },
  title: { fontSize: 22, fontWeight: 'bold', marginTop: 10, color: '#333' },
  inputContainer: {
    backgroundColor: '#fff',
    padding: 25,
    borderRadius: 15,
    elevation: 4,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 5,
  },
  inputWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    marginBottom: 20,
  },
  icon: { marginRight: 10 },
  input: { flex: 1, height: 45, fontSize: 16 },
  button: {
    backgroundColor: '#2e7d32',
    height: 50,
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  buttonText: { color: '#fff', fontSize: 16, fontWeight: 'bold' },
  // 테두리만 있는 버튼 스타일
  outlineButton: {
    flexDirection: 'row',
    height: 50,
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#2e7d32',
  },
  outlineButtonText: { color: '#2e7d32', fontSize: 16, fontWeight: 'bold' },
});
